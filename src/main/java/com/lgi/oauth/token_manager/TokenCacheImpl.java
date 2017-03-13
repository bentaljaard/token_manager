/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import static com.lgi.oauth.token_manager.GrantType.REQUIRED_PARAMETERS;
import static com.lgi.oauth.token_manager.GrantType.SUPPORTED_GRANTS;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringEntryLoader;
import net.jodah.expiringmap.ExpiringMap;
import net.jodah.expiringmap.ExpiringValue;

/**
 *
 * @author btaljaard
 */
public class TokenCacheImpl implements TokenCache {

    private final ExpiringMap<String, Token> tokenCache;
    private static final Logger logger = Logger.getLogger(TokenCacheImpl.class.getName());

    private Map params;
    private Map error;

    public TokenCacheImpl() {

        ExpirationListener<String, Token> expirationListener = new ExpirationListener<String, Token>() {
            @Override
            public void expired(String key, Token token) {
                logger.log(Level.INFO, "Token {0} expired", key);
                removeToken(key);
            }
        };

        ExpiringEntryLoader<String, Token> entryLoader = new ExpiringEntryLoader<String, Token>() {
            @Override
            public ExpiringValue<Token> load(String k) {
                //Already verified that minimum parameters are provided 
                Token token = null;
                Token refreshToken = null;
                OAuthClient client;
                GrantType grant = null;

                //Create new provider                
                Provider provider = new Provider((String) params.get("provider_url"), (String) params.get("provider_id"));

                if (SUPPORTED_GRANTS.contains(params.get("grant_type"))) {
                    GrantFactory gf = new GrantFactory();
                    grant = gf.getGrant((String) params.get("grant_type"));
                } else {
                    //set error and exit
                    error = new HashMap();
                    error.put("error", "Grant Type is not supported");
                    //map can store null, so expire entry immediately
                    return new ExpiringValue(null, 0, TimeUnit.SECONDS);
                }

                client = new OAuthClient(params, grant);
                
                

                //Does a refresh token exist in the cache
                String refreshTokenKey = k.substring(0, k.lastIndexOf("|")) + "|refresh_token";
                if (containsToken(refreshTokenKey)) {
                    logger.log(Level.INFO, "Token {0} found in cache", refreshTokenKey);

                    //get refresh token from cache
                    refreshToken = retrieveToken(refreshTokenKey);
                    try {
                        token = client.refreshToken(provider, (String) refreshToken.getProviderResponse().get("refresh_token"));
                        logger.log(Level.INFO, "Got new access token from provider using refresh token");
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, null, ex);
                        error = new HashMap();
                        error.put("error", ex.getMessage());
                        //map can store null, so expire entry immediately
                        return new ExpiringValue(null, 0, TimeUnit.SECONDS);
                    }
                } else {
                    //Authenticate to get new access token
                    try {
                        token = (Token) client.getToken(provider);
                        if (token == null) {
                            throw new Exception("Token returned is null");
                        }
                        logger.log(Level.INFO, "Got new access token from provider1");
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Failed to get token from provider", ex);
                        error = new HashMap();
                        error.put("error", ex.getMessage());
                        //map can store null, so expire entry immediately
                        return new ExpiringValue(null, 0, TimeUnit.SECONDS);
                    }
                }

                //Does response contain a refresh token, cache refresh or update what we already have
                if (token.getProviderResponse().containsKey("refresh_token")) {
                    logger.log(Level.INFO, "Response contains a refresh token");

                    if (containsToken(refreshTokenKey)) {
                        // did the token change, update or replace
                        logger.log(Level.INFO, "Token {0} found in cache", refreshTokenKey);
                        refreshToken = retrieveToken(refreshTokenKey);

                        if (!(refreshToken.getProviderResponse().get("refresh_token").equals(token.getProviderResponse().get("refresh_token")))) {
                            //replace existing refresh token
                            replaceTokenWithTTL(refreshToken, token);
                            logger.log(Level.INFO, "Replaced refresh token with a new value");
                        } else {
                            replaceToken(refreshToken, token);
                            logger.log(Level.INFO, "Refresh token value did not change, updating cache entry");
                        }
                    } else {
                        logger.log(Level.INFO, "No refresh token was found in cache, adding it");
                        refreshToken = new Token();
                        refreshToken.setClientID(token.getClientID());
                        refreshToken.setProviderID(token.getProviderID());
                        refreshToken.setScope(token.getScope());
                        refreshToken.setTTL(Integer.parseInt((String) params.get("refresh_token_ttl")));
                        refreshToken.setTokenType("refresh_token");
                        refreshToken.setProviderResponse(token.getProviderResponse());
                        cacheToken(refreshToken);
                    }

                }
                return new ExpiringValue(token, token.getTTL(), TimeUnit.SECONDS);
            }

        };
        tokenCache = ExpiringMap.builder()
                .expirationListener(expirationListener)
                .expiringEntryLoader(entryLoader)
                .variableExpiration()
                .build();
    }
    
//    private Token cacheLoader(OAuthClient client, Map params, String key){
//        
//    }

    @Override
    public void cacheToken(Token token) {
        if (token == null) {
            return;
        }
        tokenCache.put(token.getTokenCacheKey(), token, ExpirationPolicy.ACCESSED, token.getTTL(), TimeUnit.SECONDS);
    }

    @Override
    public Token retrieveToken(String key) {
        if (key == null) {
            return null;
        }
        Token token = tokenCache.get(key);
        if (token == null) {
            logger.log(Level.INFO, "Token {0} not found in cache", key);
        } else {
            logger.log(Level.INFO, "Token {0} found in cache", key);
        }
        return token;
    }

    @Override
    public Token getBearerToken(Map params) {
        Token errorToken;
        //Check if we have valid parameters to interact with the cache
        if (validParameters(params)) {
            this.params = params;
        } else {
            errorToken = new Token();
            errorToken.setTokenType("error_token");
            error = new HashMap();
            error.put("error", "Not all required parameters provided to get a token");
            errorToken.setProviderResponse(error);
            return errorToken;
        }

        Token token = retrieveToken(getTokenCacheKeyPrefix(params) + "|access_token");
        if(token == null && error == null){
            error = new HashMap();
            error.put("error", "Could not retrieve token from provider");
        }
        
        if (error != null) {
            logger.log(Level.INFO, error.toString());
            errorToken = new Token();
            errorToken.setClientID((String) params.get("client_id"));
            errorToken.setProviderID((String) params.get("provider_id"));
            errorToken.setTokenType("error_token");
            errorToken.setProviderResponse(error);
            return errorToken;
        }

        return token;

    }

    private Boolean validParameters(Map params) {
        return Util.validParameters(params, REQUIRED_PARAMETERS);

    }

    private String getTokenCacheKeyPrefix(Map params) {
        return (String) params.get("provider_id") + "|" + (String) params.get("client_id") + "|" + (String) params.get("scope");
    }

    @Override
    public Token retrieveAndRemoveToken(String key) {
        if (key == null) {
            return null;
        }
        Token token = retrieveToken(key);
        removeToken(key);
        return token;

    }

    @Override
    public void removeToken(String key) {
        if (key == null) {
            return;
        }
        tokenCache.remove(key);
    }

    @Override
    public long getTokenExpiry(String key) {
        if (key == null) {
            return -1;
        }
        return tokenCache.getExpectedExpiration(key);
    }

    @Override
    public void clearCache() {
        tokenCache.clear();
    }

    @Override
    public String toString() {
        return tokenCache.toString();
    }

    @Override
    public Boolean containsToken(String key) {
        return tokenCache.containsKey(key);
    }

    @Override
    public void replaceTokenWithTTL(Token oldToken, Token newToken) {
        if (oldToken == null || newToken == null) {
            return;
        }
        tokenCache.replace(oldToken.getTokenCacheKey(), oldToken, newToken);
        tokenCache.setExpiration(newToken.getTokenCacheKey(),newToken.getTTL(), TimeUnit.SECONDS);

    }

    @Override
    public void replaceToken(Token oldToken, Token newToken) {
        if (oldToken == null || newToken == null) {
            return;
        }
        tokenCache.replace(oldToken.getTokenCacheKey(), oldToken, newToken);
    }

    @Override
    public String getTokenExpiryTimes() {
        String result = "";
        for (Map.Entry<String, Token> entry : tokenCache.entrySet()) {
            result += (entry.getKey() + "(" + getTokenExpiry(entry.getKey()) + ")");
        }
        return result;
    }

}
