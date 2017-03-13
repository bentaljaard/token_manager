/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
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

    // FIXME
//    @Test
//    public void testRetrieveTokenNotFound() throws IllegalAccessException, NoSuchFieldException {
//        String key = "test_key2";
//
//        TokenCacheImpl instance = new TokenCacheImpl();
//        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
//        tokenCache.setAccessible(true);
//
//        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);
//
//        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
//        cache.put("test_key", token);
//
////        tokenCache.set(instance, cache);
//
//        Token result = instance.retrieveToken(key);
//        assertEquals(null, result);
//
//    }
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

    /**
     * Test of getBearerToken method, of class TokenCacheImpl.
     */
    @Test
    public void testGetBearerTokenFound() throws NoSuchFieldException, IllegalAccessException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);
        cache.put("local|test_client_1|null|access_token", token);
        tokenCache.set(instance, cache);

        Map params = new HashMap();
        params.put("provider_url", "http://test.com");
        params.put("provider_id", "local");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");

        Token expResult = token;
        Token result = instance.getBearerToken(params);
        System.out.println(result);
        assertEquals(expResult, result);

    }

    @Test
    public void testGetBearerTokenIncompleteGrantParams() throws NoSuchFieldException, IllegalAccessException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

//        cache.put("local|test_client_1|null|access_token", token);
        cache.put("test_token", token);

        //Loaded cache
        String key = "test_key";
        Map params = new HashMap();
        params.put("provider_url", "http://test.com");
        params.put("provider_id", "local");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");

        Map err = new HashMap();
        err.put("error", "The required parameters for this grant operation was not specified");
        Token expResult = new Token("test_client_1", "local", null, "error_token", 0, err);
        Token result = instance.getBearerToken(params);
        assertEquals(expResult, result);

    }

    @Test
    public void testGetBearerTokenParamError() throws NoSuchFieldException, IllegalAccessException {

        TokenCacheImpl instance = new TokenCacheImpl();
        final Field tokenCache = instance.getClass().getDeclaredField("tokenCache");
        tokenCache.setAccessible(true);

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>) tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        cache.put("local|test_client_1|null|access_token", token);

        Map params = new HashMap();
        params.put("provider_url", "http://test.com");
        params.put("provider_id", "local");
        params.put("grant_type", "client_credentials");

        Map err = new HashMap();
        err.put("error", "Not all required parameters provided to get a token");
        Token expResult = new Token(null, null, null, "error_token", 0, err);
        Token result = instance.getBearerToken(params);
        assertEquals(expResult, result);

    }

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

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);

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
        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);
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
        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);
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

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);

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

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, null);

        String key = "local|test_client_1|null|access_token";

        cache.put(key, token, ExpirationPolicy.ACCESSED, token.getTTL(), TimeUnit.SECONDS);

        //check that it was added
       
        assertTrue(cache.getExpiration(key) <= 3600000);

        Token newToken = new Token("test_client_1", "local", null, "access_token", 4000L, null);
        instance.replaceTokenWithTTL(token, newToken);

        assertEquals(cache.get(key), newToken);
        assertTrue(cache.getExpiration(key) <= 4000000 && cache.getExpiration(key) > 3600000);

        instance.replaceTokenWithTTL(newToken, null);

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

        ExpiringMap<String, Token> cache = (ExpiringMap<String, Token>)tokenCache.get(instance);

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
