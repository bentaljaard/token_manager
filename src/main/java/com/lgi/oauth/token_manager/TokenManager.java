package com.lgi.oauth.token_manager;

import com.google.common.base.Stopwatch;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenManager {

    private TokenCache cache;
    private static final Logger logger = Logger.getLogger(TokenManager.class.getName());

    public TokenManager() {
        super();
        this.setupTokenCache();

    }

    public TokenCache getTokenCache() {
        return this.cache;
    }

    private void setupTokenCache() {
        cache = new TokenCacheImpl();
        logger.log(Level.INFO, "Setup token cache");
    }

    public void clearTokenCache() {
        this.cache.clearCache();
        logger.log(Level.INFO, "Cleared token cache");
    }

    public Token getBearerToken(Map params) {
        System.out.println(params.toString());
        Stopwatch stopwatch = Stopwatch.createStarted();
        Token token = getTokenCache().getBearerToken(params);
        stopwatch.stop();
//        long millis = stopwatch.elapsed(MILLISECONDS);
        logger.log(Level.INFO, "Time to get token: {0}", stopwatch);
 
        return token;
    }

    public static void main(String[] args) {
        TokenManager tm = new TokenManager();
        Token token = null;

        Map params = new HashMap();

        try {

            
            
            

            token = tm.getBearerToken(params);
            
            System.out.println(token.toString());
            
////            Thread.sleep(5000);
//            
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
//            
//            
//            params.clear();
//            
//            params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
//            params.put("grant_type", "password");
//            params.put("client_id", "test_client_2");
//            params.put("provider_id", "local");
//            params.put("username", "test@user");
//            params.put("password", "test_password");
//            params.put("basic_username", "test_client_1");
//            params.put("basic_password", "test_secret");
//            params.put("refresh_token_ttl", "5000");
//            
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
////             Thread.sleep(5000);
//            
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
//            
//            
//            
//            params.clear();
//            
//            params.put("provider_url", "http://localhost:8080/v1/oauth/tokens"); 
//            params.put("grant_type", "client_credentials");
//            params.put("client_id", "test_client_1");
//            params.put("provider_id", "local");
//            params.put("client_secret", "test_secret");
//            params.put("basic_username", "test_client_1");
//            params.put("basic_password", "test_secret");
//
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
//            
//            
//            
//            Thread.sleep(10000);
//
//            params.clear();
//            
//            params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
//            params.put("grant_type", "password");
//            params.put("client_id", "test_client_2");
//            params.put("provider_id", "local");
//            params.put("username", "test@user");
//            params.put("password", "test_password");
//            params.put("basic_username", "test_client_1");
//            params.put("basic_password", "test_secret");
//            params.put("refresh_token_ttl", "5000");
//            
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
//            
//            params.clear();
//            
//            params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
//            params.put("grant_type", "password");
//            params.put("client_id", "test_client_2");
//            params.put("provider_id", "local");
//            params.put("username", "test@user");
//            params.put("password", "test_password");
//            params.put("basic_username", "test_client_1");
//            params.put("basic_password", "test_secret");
//            params.put("refresh_token_ttl", "5000");
//            params.put("scope","read_write");
//            
//            token = tm.getBearerToken(params);
//            
//            System.out.println(token.toString());
//            
//            
//            System.out.println(tm.getTokenCache().toString());
//            
//            TokenCache cache = tm.getTokenCache();
//            
//            System.out.print(cache.getTokenExpiryTimes());
//            
//            tm.clearTokenCache();
//            System.out.println(tm.getTokenCache().toString());
//            
            


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
