
package com.lgi.oauth.token_manager;

import java.util.Map;


public interface TokenCache {
     /**
     * Caches a Token using the Token's token property as a key and the full token instance
     * as the value.  If null is passed in as the parameter or if Token.token is null,
     * then no caching occurs.
     *
     * If Token.token already exists as a key in the cache, then its value is replaced with
     * the pass in Token instance.
     *
     * @param requestToken - The {@link com.lgi.oauth.token_manager.Token} instance that should be cached.
     */
    public void cacheToken(Token token);

    /**
     * Retrieves a cached Token whose key in the cached is the passed in String token value.
     * @param token - An expected Token.token key that should be in the cache.
     * @return A {@link com.lgi.oauth.token_manager.Token} instance retrieved from the cache, or null if
     * the token key didn't exist in the Cache.
     */
    public Token retrieveToken(String key);

    /**
     * Combines {@link OAuthTokenCacheService#retrieveToken(String)} and
     * {@link OAuthTokenCacheService#removeToken(String)} into a single call.  A Token object identified by the
     * passed in oauthToken String is retrieved from cache, removed from cache, and returned.  If a Token could not
     * be found, nothing is removed.
     *
     * @return A Token that is removed from cache after this method returns, or null or if the Token could not be found.
     */
    public Token retrieveAndRemoveToken(String key);

    /**
     * Removes the token identified by the passed in String token value from the cache.  If null
     * is passed in, no entries are removed from the cache..
     * @param token - An expected Token.token key whose entry should be removed from the cache.
     */
    public void removeToken(String key);
    
    public long getTokenExpiry(String key);
    
    public void clearCache();
    
    public String toString();
    
    public String getTokenExpiryTimes();
    
    public Boolean containsToken(String key);
    
    public void replaceTokenWithTTL(Token oldToken, Token newToken, long TTL);
    
    public void replaceToken(Token oldToken, Token newToken);
    
    public Token getBearerToken(Map params);
}
