/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author btaljaard
 */
public class OAuthClientTest {

    public OAuthClientTest() {
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
     * Test of setParams method, of class OAuthClient.
     */
    @Test
    public void testSetParams() throws NoSuchFieldException, IllegalAccessException {
        Map params = new HashMap();
        params.put("test_param", "test_value");
        OAuthClient instance = new OAuthClient(null, null);
        instance.setParams(params);

        final Field param_value = instance.getClass().getDeclaredField("params");
        param_value.setAccessible(true);
        assertEquals("Fields didn't match", param_value.get(instance), params);

    }

    /**
     * Test of setGrant method, of class OAuthClient.
     */
    @Test
    public void testSetGrant() throws NoSuchFieldException, IllegalAccessException {
        GrantType grant = new ClientCredentialsGrant();
        OAuthClient instance = new OAuthClient(null, null);
        instance.setGrant(grant);
        final Field grant_value = instance.getClass().getDeclaredField("grant");
        grant_value.setAccessible(true);
        assertEquals("Fields didn't match", grant_value.get(instance), grant);
    }

    /**
     * Test of getParams method, of class OAuthClient.
     */
    @Test
    public void testGetParams() throws NoSuchFieldException {
        Map params = new HashMap();
        params.put("test_param", "test_value");
        OAuthClient instance = new OAuthClient(params, null);
        Map result = instance.getParams();
        assertEquals(params, result);
       
    }

    /**
     * Test of getGrant method, of class OAuthClient.
     */
    @Test
    public void testGetGrant() {
        GrantType grant = new ClientCredentialsGrant();
        OAuthClient instance = new OAuthClient(null, grant);
        
        GrantType result = instance.getGrant();
        assertEquals(grant, result);
        
    }
    /////////////////////////////////////////////
    //TODO: Add test cases for password grant
    ////////////////////////////////////////////
    /**
     * Test of getToken method, of class OAuthClient.
     */
    @Test
    public void testGetTokenClientCredentialsBasicAuth() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientCredentialsSecret() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        // params.put("basic_username", "test_client_1");
        // params.put("basic_password", "test_secret");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientCredentialsWithScope() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("scope", "read_write");
        // params.put("basic_username", "test_client_1");
        // params.put("basic_password", "test_secret");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", "read_write", "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));
        urlParameters.add(new BasicNameValuePair("scope", "read_write"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientCredentialsError() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("error", "Invalid client ID or secret");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", null, "error_token", 0, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenClientCredentialsNoExpireTime() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientCredentialsAccessTokenTTL() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        params.put("access_token_ttl", "4000");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 4000, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientPasswordBasicAuth() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        params.put("username", "test_user");
        params.put("password", "test_password");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9118");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "password"));
        urlParameters.add(new BasicNameValuePair("username", "test_user"));
        urlParameters.add(new BasicNameValuePair("password", "test_password"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientPasswordBasicAuthRefresh() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        params.put("username", "test_user");
        params.put("password", "test_password");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//        urlParameters.add(new BasicNameValuePair("password", "test_password"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenClientPasswordClientSecretRefresh() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
//        params.put("basic_username", "test_client_1");
//        params.put("basic_password", "test_secret");
        params.put("username", "test_user");
        params.put("password", "test_password");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "read");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//        urlParameters.add(new BasicNameValuePair("username", "test_user"));
//        urlParameters.add(new BasicNameValuePair("password", "test_password"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }
    
    @Test
    public void testGetTokenRefreshWithError() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        params.put("username", "test_user");
        params.put("password", "test_password");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("error", "Invalid client ID or secret");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", null, "error_token", 0L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//        urlParameters.add(new BasicNameValuePair("username", "test_user"));
//        urlParameters.add(new BasicNameValuePair("password", "test_password"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }


     @Test
    public void testGetTokenRefreshWithScope() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        params.put("username", "test_user");
        params.put("password", "test_password");
        params.put("scope", "test_scope");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//        urlParameters.add(new BasicNameValuePair("username", "test_user"));
//        urlParameters.add(new BasicNameValuePair("password", "test_password"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));
        urlParameters.add(new BasicNameValuePair("scope", "test_scope"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenRefreshWithNoExpireTimeNoTTL() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        params.put("username", "test_user");
        params.put("password", "test_password");
        params.put("scope", "test_scope");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        

    }
    
    @Test
    public void testGetTokenRefreshNoExpiryWithTTL() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
        params.put("username", "test_user");
        params.put("password", "test_password");
        params.put("scope", "test_scope");
        params.put("access_token_ttl", "3600");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
//        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);

        // Verify request to provider
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//        urlParameters.add(new BasicNameValuePair("username", "test_user"));
//        urlParameters.add(new BasicNameValuePair("password", "test_password"));
        urlParameters.add(new BasicNameValuePair("client_id", "test_client_1"));
        urlParameters.add(new BasicNameValuePair("client_secret", "test_secret"));
        urlParameters.add(new BasicNameValuePair("scope", "test_scope"));

//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_client_1", "test_secret");
        UsernamePasswordCredentials credentials = null;
        verify(provider).getResponse(headers, urlParameters, credentials);

        // Verify token response
        assertEquals(expResult, result);

    }
    
    
    @Test(expected = UnsupportedOperationException.class)
    public void testGetTokenRefreshWrongGrant() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
//        params.put("username", "test_user");
//        params.put("password", "test_password");
//        params.put("scope", "test_scope");
//        params.put("access_token_ttl", "3600");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);


    }
    
     @Test(expected = IllegalArgumentException.class)
    public void testGetTokenRefreshWrongParams() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");
//        params.put("username", "test_user");
//        params.put("password", "test_password");
//        params.put("scope", "test_scope");
//        params.put("access_token_ttl", "3600");

        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        OAuthClient instance = new OAuthClient(params, new PasswordGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);


    }
    
      @Test(expected = UnsupportedOperationException.class)
    public void testGetTokenRefreshUnsupportedGrant() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");


        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        GrantType testGrant = mock(GrantType.class);
        when(testGrant.getType()).thenReturn("testGrant");
        
        OAuthClient instance = new OAuthClient(params, testGrant);

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.refreshToken(provider, refreshToken);


    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testGetTokeUnsupportedGrant() throws Exception {

        String refreshToken = "bc3cb409-2aea-4707-b953-9d6e287d9118";

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");


        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("user_id", "2");
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");
        dummyResponse.put("refresh_token", "bc3cb409-2aea-4707-b953-9d6e287d9120");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        GrantType testGrant = mock(GrantType.class);
        when(testGrant.getType()).thenReturn("testGrant");
        
        OAuthClient instance = new OAuthClient(params, testGrant);

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);


    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testGetTokeUnsupportedGrantOperation() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");


        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        GrantType testGrant = mock(ClientCredentialsGrant.class);
        when(testGrant.getType()).thenReturn("client_credentials");
        when(testGrant.getSupportedOperations()).thenReturn(Arrays.asList("test"));
        
        OAuthClient instance = new OAuthClient(params, testGrant);

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);


    }
    
     @Test(expected = IllegalArgumentException.class)
    public void testGetTokenInvalidArguments() throws Exception {

        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
//        params.put("client_id", "test_client_1");
        params.put("client_secret", "test_secret");
        params.put("provider_id", "local");


        Provider provider = mock(Provider.class);
        Map dummyResponse = new HashMap();
        dummyResponse.put("access_token", "a4cf9dee-3ea4-412f-af63-f2bac6faab33");
        dummyResponse.put("expires_in", "3600");
        dummyResponse.put("token_type", "Bearer");
        dummyResponse.put("scope", "test_scope");

        when(provider.getResponse((List<NameValuePair>) (List<?>) anyList(), (List<NameValuePair>) (List<?>) anyList(), (UsernamePasswordCredentials) any())).thenReturn(dummyResponse);

        
        OAuthClient instance = new OAuthClient(params, new ClientCredentialsGrant());

        Token expResult = new Token("test_client_1", "local", "test_scope", "access_token", 3600L, dummyResponse);

        Token result = instance.getToken(provider);


    }

}
