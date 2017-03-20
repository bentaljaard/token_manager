/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author btaljaard
 */
public class TokenCacheImplTest {

    public TokenCacheImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of cacheToken method, of class TokenCacheImpl.
     */
    @Test
    public void testCacheTokenExists() throws NoSuchFieldException, IllegalAccessException {
        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        TokenCacheImpl instance = new TokenCacheImpl();
        instance.cacheToken(token);

        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
        assertTrue(cache.containsKey("local|test_client_1|null|access_token"));

    }

    @Test
    public void testCacheTokenNotExist() throws NoSuchFieldException, IllegalAccessException {
        Token token = null;
        TokenCacheImpl instance = new TokenCacheImpl();
        instance.cacheToken(token);

        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
        assertFalse(cache.containsKey(null));

    }
//

    /**
     * Test of retrieveToken method, of class TokenCacheImpl.
     */
    @Test
    public void testRetrieveTokenFound() throws IllegalAccessException, NoSuchFieldException {
        String key = "test_key";

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);

        Token result = instance.retrieveToken(key);
        assertEquals(token, result);

    }
    
     @Test
    public void testRetrieveTokenNotFound() throws IllegalAccessException, NoSuchFieldException {
        String key = "test_key2";

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);

        Token result = instance.retrieveToken(key);
        assertEquals(null, result);

    }
    
     @Test
    public void testRetrieveTokenExpired() throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        String key = "test_key";

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put(key, token);
        cache.setExpiration(key, 0, TimeUnit.SECONDS);
        Thread.sleep(1000);

        Token result = instance.retrieveToken(key);
        assertEquals(null, result);

    }
    
    @Test
    public void testRetrieveAndRemoveTokenNull() throws IllegalAccessException, NoSuchFieldException {
        String key = null;

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);

        Token result = instance.retrieveAndRemoveToken(key);
        assertEquals(null, result);

    }

   
    @Test
    public void testRetrieveTokenNull() throws IllegalAccessException, NoSuchFieldException {
        String key = null;
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        cache.put("test_key", token);
        Token result = instance.retrieveToken(key);
        assertEquals(null, result);

    }

//    /**
//     * Test of getBearerToken method, of class TokenCacheImpl.
//     */
//    @Test
//    public void testGetBearerTokenFound() throws NoSuchFieldException, IllegalAccessException {
//
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
//        cache.put("local|test_client_1|null|access_token", token);
//        tokenCache.set(instance, cache);
//
//        Map params = new HashMap();
//        params.put("provider_url", "http://test.com");
//        params.put("provider_id", "local");
//        params.put("grant_type", "client_credentials");
//        params.put("client_id", "test_client_1");
//
//        Token expResult = token;
//        Token result = instance.getBearerToken(params);
//        System.out.println(result);
//        assertEquals(expResult, result);
//
//    }

//    @Test
//    public void testGetBearerTokenIncompleteGrantParams() throws NoSuchFieldException, IllegalAccessException {
//
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
//
////        cache.put("local|test_client_1|null|access_token", token);
//        cache.put("test_token", token);
//
//        //Loaded cache
//        String key = "test_key";
//        Map params = new HashMap();
//        params.put("provider_url", "http://test.com");
//        params.put("provider_id", "local");
//        params.put("grant_type", "client_credentials");
//        params.put("client_id", "test_client_1");
//
//        Map err = new HashMap();
//        err.put("error", "The required parameters for this grant operation was not specified");
//        Token expResult = new Token("test_client_1", "local", null, "error_token", 0, err);
//        Token result = instance.getBearerToken(params);
//        assertEquals(expResult, result);
//
//    }
//
//    @Test
//    public void testGetBearerTokenParamError() throws NoSuchFieldException, IllegalAccessException {
//
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
//
//        cache.put("local|test_client_1|null|access_token", token);
//
//        Map params = new HashMap();
//        params.put("provider_url", "http://test.com");
//        params.put("provider_id", "local");
//        params.put("grant_type", "client_credentials");
//
//        Map err = new HashMap();
//        err.put("error", "Not all required parameters provided to get a token");
//        Token expResult = new Token(null, null, null, "error_token", 0, err);
//        Token result = instance.getBearerToken(params);
//        assertEquals(expResult, result);
//
//    }
//
//    @Test
//    public void testLoadCacheNullToken() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//
//        //Get access to the token cache
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        //Setup reflection to test the private method
//        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
//        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
//        loadCache.setAccessible(true);
//
//        //Setup mock objects
//        OAuthClient mockOAuthClient = mock(OAuthClient.class);
//        Provider mockProvider = mock(Provider.class);
//
//        //Setup params
//        String key = "local|test_client_1|null|access_token";
//        Map params = new HashMap();
//        params.put("provider_url", "http://test.com");
//        params.put("provider_id", "local");
//        params.put("grant_type", "client_credentials");
//        params.put("client_id", "test_client_1");
//
//        //Invoke method
//        Object[] parameters = new Object[]{mockOAuthClient, mockProvider, params, key};
//        ExpiringValue result = (ExpiringValue) loadCache.invoke(instance, parameters);
//
//        Object expValue = null;
//        assertEquals(expValue, result.getValue());
//
//    }

//    @Test
//    public void testLoadCacheToken() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {
//
//        //Get access to the token cache
//        TokenCacheImpl instance = new TokenCacheImpl();
//
//        //Setup reflection to test the private method
//        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
//        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
//        loadCache.setAccessible(true);
//
//        //Setup params
//        String key = "local|test_client_1|null|access_token";
//
//        //Setup mock objects
//        OAuthClient mockOAuthClient = mock(OAuthClient.class);
//
//        Map dummyResponse = new HashMap();
//        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
//        dummyResponse.put("token_type", "Bearer");
//        dummyResponse.put("scope", "read");
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
//
//        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);
//
//        Provider mockProvider = mock(Provider.class);
//
//        //Invoke method
//        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, new HashMap(), key};
//        ExpiringValue result = (ExpiringValue) loadCache.invoke(instance, methodParams);
//
//        System.out.println(result.toString());
//        assertEquals(token, result.getValue());
//        assertEquals(null, result.getExpirationPolicy());
//        assertEquals(3600, result.getDuration());
//        assertEquals(TimeUnit.SECONDS, result.getTimeUnit());
//    }
    
//     @Test
//    public void testLoadCacheTokenNewRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {
//
//        //Get access to the token cache
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        //Setup reflection to test the private method
//        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
//        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
//        loadCache.setAccessible(true);
//
//        //Setup params
//        String key = "local|test_client_1|null|access_token";
//
//        //Setup mock objects
//        OAuthClient mockOAuthClient = mock(OAuthClient.class);
//
//        Map dummyResponse = new HashMap();
//        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
//        dummyResponse.put("token_type", "Bearer");
//        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("scope", "read");
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
//
//        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);
//
//        Provider mockProvider = mock(Provider.class);
//
//        //Invoke method
//         Map params = new HashMap();
//        params.put("refresh_token_ttl", "4000");
//        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
//        ExpiringValue result = (ExpiringValue) loadCache.invoke(instance, methodParams);
//
////        System.out.println(result.toString());
//        assertEquals(token, result.getValue());
//        assertEquals(null, result.getExpirationPolicy());
//        assertEquals(3600, result.getDuration());
//        assertEquals(TimeUnit.SECONDS, result.getTimeUnit());
//        
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//        
//        //Also assert that refresh token in cache was updated
//        Token newRefreshToken = cache.get("local|test_client_1|null|refresh_token");
//        assertEquals("a4cf9dee-3ea4-412f-af63-f2bac6faab33", newRefreshToken.getProviderResponse().get("refresh_token"));
//        assertEquals(4000, newRefreshToken.getTTL());
//        assertEquals(4000, cache.getExpiration("local|test_client_1|null|refresh_token")/1000);
//        
//        
//        
//    }

//    @Test
//    public void testRefreshTokenWithRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {
//
//        //Get access to the token cache
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//        
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//
//        Map dummyResponse = new HashMap();
//        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
//        dummyResponse.put("token_type", "Bearer");
//        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("scope", "read");
//
//        Token token = new Token("test_client_1", "local", null, "refresh_token", 3600L, dummyResponse);
//
//        cache.put("local|test_client_1|null|refresh_token", token);
//
//        //Setup reflection to test the private method
//        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
//        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
//        loadCache.setAccessible(true);
//
//        //Setup params
//        String key = "local|test_client_1|null|access_token";
//
//        //Setup mock objects
//        OAuthClient mockOAuthClient = mock(OAuthClient.class);
//
//        String refreshToken = "a4cf9dee-3ea4-412f-af63-f2bac6faab33";
//        Token newToken = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
//
//        when(mockOAuthClient.refreshToken((Provider) any(), eq((String)refreshToken))).thenReturn(newToken);
//
//        Provider mockProvider = mock(Provider.class);
//
//        //Invoke method
//        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, new HashMap(), key};
//        ExpiringValue result = (ExpiringValue) loadCache.invoke(instance, methodParams);
//
////        System.out.println(result.toString());
//        assertEquals(newToken, result.getValue());
//        assertEquals(null, result.getExpirationPolicy());
//        assertEquals(3600, result.getDuration());
//        assertEquals(TimeUnit.SECONDS, result.getTimeUnit());
//    }
//    
//    @Test
//    public void testRefreshTokenWithNewRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {
//
//        //Get access to the token cache
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
//
//        Map dummyResponse = new HashMap();
//        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
//        dummyResponse.put("token_type", "Bearer");
//        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("scope", "read");
//
//        Token token = new Token("test_client_1", "local", null, "refresh_token", 3600L, dummyResponse);
//
//        cache.put("local|test_client_1|null|refresh_token", token);
//
//        //Setup reflection to test the private method
//        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
//        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
//        loadCache.setAccessible(true);
//
//        //Setup params
//        String key = "local|test_client_1|null|access_token";
//
//        //Setup mock objects       
//        OAuthClient mockOAuthClient = mock(OAuthClient.class);
//
//        String refreshToken = "a4cf9dee-3ea4-412f-af63-f2bac6faab33";
//        
//        Map newRefresh = new HashMap();
//        newRefresh.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
//        newRefresh.put("expires_in", "3600");
//        newRefresh.put("token_type", "Bearer");
//        newRefresh.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
//        newRefresh.put("scope", "read");
//        Token newToken = new Token("test_client_1", "local", null, "access_token", 3600L, newRefresh);
//
//        when(mockOAuthClient.refreshToken((Provider) any(), eq((String)refreshToken))).thenReturn(newToken);
//
//        Provider mockProvider = mock(Provider.class);
//
//        //Invoke method
//        Map params = new HashMap();
//        params.put("refresh_token_ttl", "4000");
//        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
//        ExpiringValue result = (ExpiringValue) loadCache.invoke(instance, methodParams);
//
////        System.out.println(result.toString());
//        //Assert that the correct access token was returned
//        assertEquals(newToken, result.getValue());
//        assertEquals(null, result.getExpirationPolicy());
//        assertEquals(3600, result.getDuration());
//        assertEquals(TimeUnit.SECONDS, result.getTimeUnit());
//        
//        //Also assert that refresh token in cache was updated
//        Token newRefreshToken = cache.get("local|test_client_1|null|refresh_token");
//        assertEquals("a4cf9dee-3ea4-412f-af63-f2bac6faab34", newRefreshToken.getProviderResponse().get("refresh_token"));
//        assertEquals(4000, newRefreshToken.getTTL());
//        assertEquals(4000, cache.getExpiration("local|test_client_1|null|refresh_token")/1000);
////        System.out.println(cache.getExpiration("local|test_client_1|null|refresh_token"));
//        
//    }
//    
   


    /**
     * Test of retrieveAndRemoveToken method, of class TokenCacheImpl.
     */
    @Test
    public void testRetrieveAndRemoveToken() throws NoSuchFieldException, IllegalAccessException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);
        String key = "test_key";
        //check that it was added
        assertTrue(cache.containsKey(key));
        Token result = instance.retrieveAndRemoveToken(key);
        assertFalse(cache.containsKey(key));
        assertEquals(token, result);

    }

    /**
     * Test of removeToken method, of class TokenCacheImpl.
     */
    @Test
    public void testRemoveToken() throws IllegalAccessException, NoSuchFieldException {
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);
        String key = "test_key";
        //check that it was added
        assertTrue(cache.containsKey(key));

        instance.removeToken(key);

        assertFalse(cache.containsKey(key));

    }

    @Test
    public void testRemoveTokenNull() throws IllegalAccessException, NoSuchFieldException {
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);
        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        cache.put("test_key", token);

        String key = "test_key";
        assertTrue(cache.containsKey(key));

        instance.removeToken(null);

        assertTrue(cache.containsKey(key));

    }

    /**
     * Test of getTokenExpiry method, of class TokenCacheImpl.
     */
    @Test
    public void testGetTokenExpiry() throws IllegalAccessException, NoSuchFieldException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);

        String key = "test_key";

        long expResult = 60000;
        long result = instance.getTokenExpiry(key);

        assertTrue(expResult >= result);

        key = null;
        assertEquals(-1, instance.getTokenExpiry(key));

    }

    /**
     * Test of clearCache method, of class TokenCacheImpl.
     */
    @Test
    public void testClearCache() throws NoSuchFieldException, IllegalAccessException {
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);
        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        cache.put("test_key", token);
        assertFalse(cache.isEmpty());
        instance.clearCache();
        assertTrue(cache.isEmpty());

    }

    /**
     * Test of toString method, of class TokenCacheImpl.
     */
    @Test
    public void testToStringEmpty() {
        TokenCacheImpl instance = new TokenCacheImpl();
        String expResult = "{}";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

    @Test
    public void testToStringValues() throws NoSuchFieldException, IllegalAccessException {
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);
        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);
        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        cache.put("test_key", token);
        String expResult = "{test_key=clientID: test_client_1, providerID: local, scope: null, tokenType: access_token, ttl: 3600, providerResponse: null}";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

    /**
     * Test of containsToken method, of class TokenCacheImpl.
     */
    @Test
    public void testContainsToken() throws NoSuchFieldException, IllegalAccessException {

        String key = "test_key";
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("test_key", token);

        Boolean expResult = true;
        Boolean result = instance.containsToken(key);
        assertEquals(expResult, result);

    }

    /**
     * Test of replaceTokenWithTTL method, of class TokenCacheImpl.
     */
    @Test
    public void testReplaceTokenWithTTL() throws NoSuchFieldException, IllegalAccessException {
        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        String key = "local|test_client_1|null|access_token";

        cache.put(key, token, ExpirationPolicy.ACCESSED, token.getTTL(), TimeUnit.SECONDS);

        //check that it was added
        assertTrue(cache.getExpiration(key) <= 3600000);

        Token newToken = new Token("test_client_1", "local", null, "access_token", 4000L, null);
        instance.replaceTokenWithTTL(token, newToken, 4000L);

        assertEquals(cache.get(key), newToken);
        assertTrue(cache.getExpiration(key) <= 4000000 && cache.getExpiration(key) > 3600000);

        instance.replaceTokenWithTTL(newToken, null, 4000L);

        assertEquals(cache.get(key), newToken);

    }

    /**
     * Test of replaceToken method, of class TokenCacheImpl.
     */
    @Test
    public void testReplaceToken() throws NoSuchFieldException, IllegalAccessException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        String key = "local|test_client_1|null|access_token";

        cache.put(key, token);

        //check that it was added
        assertTrue(cache.containsKey(key));

        Token newToken = new Token("test_client_1", "local", null, "access_token", 4000L, null);
        instance.replaceToken(token, newToken);

        assertEquals(cache.get(key), newToken);

        instance.replaceToken(newToken, null);

        assertEquals(cache.get(key), newToken);

    }
//
//    /**
//     * Test of getTokenExpiryTimes method, of class TokenCacheImpl.
//     */
//    @Test
//    public void testGetTokenExpiryTimes() {
//        System.out.println("getTokenExpiryTimes");
//        TokenCacheImpl instance = new TokenCacheImpl();
//        String expResult = "";
//        String result = instance.getTokenExpiryTimes();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
