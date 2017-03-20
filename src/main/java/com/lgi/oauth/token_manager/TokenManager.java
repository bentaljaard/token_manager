package com.lgi.oauth.token_manager;

import com.google.common.base.Stopwatch;
import static com.lgi.oauth.token_manager.GrantType.REQUIRED_PARAMETERS;
import static com.lgi.oauth.token_manager.GrantType.SUPPORTED_GRANTS;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.auth.AuthenticationException;

public class TokenManager {

    private TokenCache cache;
    private static final Logger logger = Logger.getLogger(TokenManager.class.getName());
    private static final Object CACHE_LOCK = new Object();

    public TokenManager() {
        super();
        this.setupTokenCache();

    }

    private Token loadCache(OAuthClient client, Provider provider, Map params, String k) throws AuthenticationException, IOException, Exception {
        Token token;
        Token refreshToken = null;
        
        //Check if token has not been added to the cache by another thread
        if(this.getTokenCache().containsToken(k)){
            return this.getTokenCache().retrieveToken(k);
        }
        
        //Does a refresh token exist in the cache
        String refreshTokenKey = k.substring(0, k.lastIndexOf("|")) + "|refresh_token";
        boolean hasRefreshToken = this.getTokenCache().containsToken(refreshTokenKey);
        
        if (hasRefreshToken) {
            logger.log(Level.FINE, "Token {0} found in cache", refreshTokenKey);

            //get refresh token from cache
            refreshToken = this.getTokenCache().retrieveToken(refreshTokenKey);

            token = client.refreshToken(provider, (String) refreshToken.getProviderResponse().get("refresh_token"));
             if (token == null) {
                throw new Exception("Token returned is null");
            }
            logger.log(Level.INFO, "Got new access token from provider using refresh token");

        } else {
            //Authenticate to get new access token

            token = (Token) client.getToken(provider);
            if (token == null) {
                throw new Exception("Token returned is null");
            }
            logger.log(Level.INFO, "Got new access token from provider {0}", provider.getID());

        }
        System.out.println(token.toString());
        //Does response contain a refresh token, cache refresh or update what we already have
        if (token.getProviderResponse()
                .containsKey("refresh_token")) {
            logger.log(Level.FINE, "Response contains a refresh token");

            if (hasRefreshToken) {
                // did the token change, update or replace
//                logger.log(Level.FINE, "Token {0} found in cache", refreshTokenKey);
//                refreshToken = this.getTokenCache().retrieveToken(refreshTokenKey);

                if (!(refreshToken.getProviderResponse().get("refresh_token").equals(token.getProviderResponse().get("refresh_token")))) {
                    //replace existing refresh token
                    Token newToken = new Token();
                    newToken.setClientID(token.getClientID());
                    newToken.setProviderID(token.getProviderID());
                    newToken.setScope(token.getScope());
                    newToken.setTTL(Integer.parseInt((String) params.get("refresh_token_ttl")));
                    newToken.setTokenType("refresh_token");
                    newToken.setProviderResponse(token.getProviderResponse());
                    this.getTokenCache().replaceTokenWithTTL(refreshToken, newToken, Integer.parseInt((String) params.get("refresh_token_ttl")));
                    logger.log(Level.INFO, "Updated refresh token with a new value");
                } else {
                    this.getTokenCache().replaceToken(refreshToken, token);
                    logger.log(Level.INFO, "Refresh token still valid, updating access token");
                }
            } else {
                logger.log(Level.FINE, "No refresh token was found in cache, adding it");
                refreshToken = new Token();
                refreshToken.setClientID(token.getClientID());
                refreshToken.setProviderID(token.getProviderID());
                refreshToken.setScope(token.getScope());
                refreshToken.setTTL(Integer.parseInt((String) params.get("refresh_token_ttl")));
                refreshToken.setTokenType("refresh_token");
                refreshToken.setProviderResponse(token.getProviderResponse());
                this.getTokenCache().cacheToken(refreshToken);
                logger.log(Level.INFO, "Added refresh token to the token cache");
            }

        }
        this.getTokenCache().cacheToken(token);
        logger.log(Level.INFO, "Added access token to the token cache");
        return token;
    }

    private Token getToken(OAuthClient client) {
        Token errorToken;
        Token token;
        //Check if we have valid parameters to interact with the cache
        if (!validParameters(client.getParams())) {
            errorToken = new Token();
            errorToken.setTokenType("error_token");
            Map error = new HashMap();
            error.put("error", "Not all required parameters provided to get a token");
            errorToken.setProviderResponse(error);
            return errorToken;
        }

        token = this.getTokenCache().retrieveToken(getTokenCacheKeyPrefix(client.getParams()) + "|access_token");

        //if token cannot be found in cache, then get it from the provider and populate cache
        if (token == null) {
            //Create new provider                
            Provider provider = new Provider((String) client.getParams().get("provider_url"), (String) client.getParams().get("provider_id"));
            
            try {
                synchronized (CACHE_LOCK) {
                    token = this.loadCache(client, provider, client.getParams(), getTokenCacheKeyPrefix(client.getParams()) + "|access_token");
                }
            } catch (Exception ex) {
                Logger.getLogger(TokenCacheImpl.class.getName()).log(Level.SEVERE, null, ex);
                Map error = new HashMap();
                error.put("error", ex.getMessage());
                errorToken = new Token();
                errorToken.setTokenType("error_token");
                errorToken.setProviderResponse(error);
                return errorToken;
            }
        }

        return token;

    }

    public TokenCache getTokenCache() {
        return this.cache;
    }

    private Boolean validParameters(Map params) {
        return Util.validParameters(params, REQUIRED_PARAMETERS);

    }

    private String getTokenCacheKeyPrefix(Map params) {
        return (String) params.get("provider_id") + "|" + (String) params.get("client_id") + "|" + (String) params.get("scope");
    }

    private void setupTokenCache() {
        cache = new TokenCacheImpl();
        logger.log(Level.INFO, "Setup token cache");
    }

    public void clearTokenCache() {
        this.cache.clearCache();
        logger.log(Level.INFO, "Cleared token cache");
    }

    public Token getBearerToken(OAuthClient client) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Token token = getToken(client);
        stopwatch.stop();
        logger.log(Level.FINE, "Time to get token: {0}", stopwatch);

        return token;
    }

}
