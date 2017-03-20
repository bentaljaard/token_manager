/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.auth.AuthenticationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author btaljaard
 */
public class TokenManagerTest {

    public TokenManagerTest() {
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
     * Test of getTokenCache method, of class TokenManager.
     */
    @Test
    public void testGetTokenCache() {
        TokenManager instance = new TokenManager();
        assertTrue(instance.getTokenCache() instanceof TokenCacheImpl);

    }

    /**
     * Test of clearTokenCache method, of class TokenManager.
     */
    @Test
    public void testClearTokenCache() {
        TokenManager instance = new TokenManager();
        TokenCache cache = instance.getTokenCache();
        String key = "local|test_client_1|null|access_token";
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
        cache.cacheToken(token);
        assertTrue(cache.containsToken(key));

        instance.clearTokenCache();
        assertFalse(cache.containsToken(key));
    }

    /**
     * Test of getBearerToken method, of class TokenManager.
     */
    @Test
    public void testGetBearerTokenInvalidParams() {
        Map params = null;
        TokenManager instance = new TokenManager();
        OAuthClient client = new OAuthClient(params, new ClientCredentialsGrant());
        Token result = instance.getBearerToken(client);
        Map dummyResponse = new HashMap();
        dummyResponse.put("error", "Not all required parameters provided to get a token");
        

        Token token = new Token(null, null, null, "error_token", 0L, dummyResponse);
        assertEquals(token, result);
      
    }
    
    @Test
    public void testGetBearerTokenFromCache() {
        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        
        
        TokenManager instance = new TokenManager();
        TokenCache cache = instance.getTokenCache();
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
        
        cache.cacheToken(token);
        OAuthClient client =  new OAuthClient(params,new ClientCredentialsGrant());
        Token result = instance.getBearerToken(client);
       
        assertEquals(token, result);
      
    }
    
    @Test
    public void testGetBearerToken() throws AuthenticationException, IOException {
        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        
        
        TokenManager instance = new TokenManager();
        TokenCache cache = instance.getTokenCache();
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);
                
        
        OAuthClient client =  mock(OAuthClient.class);
        when(client.getToken((Provider) any())).thenReturn(token);
        when(client.getParams()).thenReturn(params);
        when(client.getGrant()).thenReturn(new ClientCredentialsGrant());
        
        Token result = instance.getBearerToken(client);
        System.out.println(result.toString());
       
        assertEquals(token, result);
      
    }
    
    @Test
    public void testGetBearerTokenError() throws AuthenticationException, IOException {
        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        
        
        TokenManager instance = new TokenManager();
        TokenCache cache = instance.getTokenCache();
        Map dummyResponse = new HashMap();
        dummyResponse.put("error", "Token returned is null");
      

        Token token = new Token(null, null, null, "error_token", 0L, dummyResponse);
                
        
        OAuthClient client =  mock(OAuthClient.class);
//        when(client.getToken((Provider) any())).thenThrow(new Exception("Some error occured"));
        when(client.getParams()).thenReturn(params);
        when(client.getGrant()).thenReturn(new ClientCredentialsGrant());
        
        Token result = instance.getBearerToken(client);
        System.out.println(result.toString());
       
        assertEquals(token, result);
      
    }
    
    
    @Test
    public void testLoadCacheToken() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, new HashMap(), key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        System.out.println(result.toString());
        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.getTokenExpiry(key) < 3600000);
        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }

    @Test(expected = Exception.class)
    public void testLoadCacheTokenNull() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        when(mockOAuthClient.getToken((Provider) any())).thenReturn(null);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, new HashMap(), key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        System.out.println(result.toString());
        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.getTokenExpiry(key) < 3600000);
        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }

    @Test
    public void testLoadCacheTokenExists() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        cache.cacheToken(token);

        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, new HashMap(), key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        System.out.println(result.toString());
        assertEquals(token, result);

        assertTrue(cache.containsToken(key));

    }

    @Test
    public void testLoadCacheTokenRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";
        String refreshKey = "local|test_client_1|null|refresh_token";

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Map params = new HashMap();
        params.put("refresh_token_ttl", "4000");
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.containsToken(refreshKey));
//        assertTrue(cache.getTokenExpiry(key) < 4000000);
//        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }

    @Test
    public void testLoadCacheTokenExistingRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";
        String refreshKey = "local|test_client_1|null|refresh_token";

        //Load refresh token into cache
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("scope", "read");

        Token refreshtoken = new Token("test_client_1", "local", null, "refresh_token", 3600L, dummyResponse);
        cache.cacheToken(refreshtoken);

        System.out.println(cache.containsToken(refreshKey));

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummytokenResponse = new HashMap();
        dummytokenResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
        dummytokenResponse.put("expires_in", "3600");
        dummytokenResponse.put("token_type", "Bearer");
        dummytokenResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummytokenResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummytokenResponse);

//        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);
        when(mockOAuthClient.refreshToken((Provider) any(), (String) any())).thenReturn(token);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Map params = new HashMap();
        params.put("refresh_token_ttl", "4000");
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.containsToken(refreshKey));
//        assertTrue(cache.getTokenExpiry(key) < 4000000);
//        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }

    @Test(expected = Exception.class)
    public void testLoadCacheTokenNullRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";
        String refreshKey = "local|test_client_1|null|refresh_token";

        //Load refresh token into cache
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("scope", "read");

        Token refreshtoken = new Token("test_client_1", "local", null, "refresh_token", 3600L, dummyResponse);
        cache.cacheToken(refreshtoken);

        System.out.println(cache.containsToken(refreshKey));

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummytokenResponse = new HashMap();
        dummytokenResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
        dummytokenResponse.put("expires_in", "3600");
        dummytokenResponse.put("token_type", "Bearer");
        dummytokenResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummytokenResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummytokenResponse);

//        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);
        when(mockOAuthClient.refreshToken((Provider) any(), (String) any())).thenReturn(null);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Map params = new HashMap();
        params.put("refresh_token_ttl", "4000");
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        System.out.println(result.toString());
        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.containsToken(refreshKey));
//        assertTrue(cache.getTokenExpiry(key) < 4000000);
//        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }

    @Test
    public void testLoadCacheTokenUpdatecdRefresh() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, AuthenticationException, IOException {

        //Get access to the token cache
        TokenManager instance = new TokenManager();

        TokenCache cache = instance.getTokenCache();

        //Setup reflection to test the private method
        Class[] argTypes = new Class[]{OAuthClient.class, Provider.class, Map.class, String.class};
        final Method loadCache = instance.getClass().getDeclaredMethod("loadCache", argTypes);
        loadCache.setAccessible(true);

        //Setup params
        String key = "local|test_client_1|null|access_token";
        String refreshKey = "local|test_client_1|null|refresh_token";

        //Load refresh token into cache
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("scope", "read");

        Token refreshtoken = new Token("test_client_1", "local", null, "refresh_token", 3600L, dummyResponse);
        cache.cacheToken(refreshtoken);

        System.out.println(cache.containsToken(refreshKey));

        //Setup mock objects
        OAuthClient mockOAuthClient = mock(OAuthClient.class);

        Map dummytokenResponse = new HashMap();
        dummytokenResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
        dummytokenResponse.put("expires_in", "3600");
        dummytokenResponse.put("token_type", "Bearer");
        dummytokenResponse.put("refresh_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab34");
        dummytokenResponse.put("scope", "read");

        Token token = new Token("test_client_1", "local", null, "access_token", 3600L, dummytokenResponse);

//        when(mockOAuthClient.getToken((Provider) any())).thenReturn(token);
        when(mockOAuthClient.refreshToken((Provider) any(), (String) any())).thenReturn(token);

        Provider mockProvider = mock(Provider.class);

        //Invoke method
        Map params = new HashMap();
        params.put("refresh_token_ttl", "4000");
        Object[] methodParams = new Object[]{mockOAuthClient, mockProvider, params, key};
        Token result = (Token) loadCache.invoke(instance, methodParams);

        System.out.println(result.toString());
        assertEquals(token, result);

        assertTrue(cache.containsToken(key));
        assertTrue(cache.containsToken(refreshKey));
//        assertTrue(cache.getTokenExpiry(key) < 4000000);
//        assertTrue(cache.getTokenExpiry(key) > 3500000);

    }
}
