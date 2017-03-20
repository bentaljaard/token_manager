
package com.lgi.oauth.token_manager;


public interface TokenCache {
   
    public void cacheToken(Token token);

   
    public Token retrieveToken(String key);

    public Token retrieveAndRemoveToken(String key);

   
    public void removeToken(String key);
    
    public long getTokenExpiry(String key);
    
    public void clearCache();
    
    @Override
    public String toString();
        
    public Boolean containsToken(String key);
    
    public void replaceTokenWithTTL(Token oldToken, Token newToken, long TTL);
    
    public void replaceToken(Token oldToken, Token newToken);
    
}
