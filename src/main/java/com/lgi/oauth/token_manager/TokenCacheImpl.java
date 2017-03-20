package com.lgi.oauth.token_manager;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

/**
 *
 * @author btaljaard
 */
public class TokenCacheImpl implements TokenCache {

    private final ExpiringMap<String, Token> tokenCache;
    private static final Logger logger = Logger.getLogger(TokenCacheImpl.class.getName());

    public TokenCacheImpl() {

        ExpirationListener<String, Token> expirationListener = new ExpirationListener<String, Token>() {
            @Override
            public void expired(String key, Token token) {
                logger.log(Level.INFO, "Token {0} expired", key);
                removeToken(key);
            }
        };

        tokenCache = ExpiringMap.builder()
                .expirationListener(expirationListener)
                .variableExpiration()
                .build();
    }
    

    @Override
    public void cacheToken(Token token) {
        if (token == null) {
            return;
        }
        tokenCache.put(token.getTokenCacheKey(), token, ExpirationPolicy.CREATED, token.getTTL(), TimeUnit.SECONDS);
    }

    @Override
    public Token retrieveToken(String key) {
        if (key == null) {
            return null;
        }
        Token token = tokenCache.get(key);
        if (token == null) {
            logger.log(Level.FINE, "Token {0} not found in cache", key);
        } else {
            logger.log(Level.FINE, "Token {0} found in cache", key);
        }
        return token;
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
    public void replaceTokenWithTTL(Token oldToken, Token newToken, long TTL) {
        if (oldToken == null || newToken == null) {
            return;
        }
        tokenCache.replace(oldToken.getTokenCacheKey(), oldToken, newToken);
        tokenCache.setExpirationPolicy(ExpirationPolicy.CREATED);
        tokenCache.setExpiration(newToken.getTokenCacheKey(), TTL, TimeUnit.SECONDS);

    }

    @Override
    public void replaceToken(Token oldToken, Token newToken) {
        if (oldToken == null || newToken == null) {
            return;
        }
        tokenCache.replace(oldToken.getTokenCacheKey(), oldToken, newToken);
    }

   
}
