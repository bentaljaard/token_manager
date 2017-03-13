/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.util.ArrayList;
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
//
//    /**
//     * Test of setParams method, of class OAuthClient.
//     */
//    @Test
//    public void testSetParams() {
//        System.out.println("setParams");
//        Map params = null;
//        OAuthClient instance = null;
//        instance.setParams(params);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGrant method, of class OAuthClient.
//     */
//    @Test
//    public void testSetGrant() {
//        System.out.println("setGrant");
//        GrantType grant = null;
//        OAuthClient instance = null;
//        instance.setGrant(grant);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getParams method, of class OAuthClient.
//     */
//    @Test
//    public void testGetParams() {
//        System.out.println("getParams");
//        OAuthClient instance = null;
//        Map expResult = null;
//        Map result = instance.getParams();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGrant method, of class OAuthClient.
//     */
//    @Test
//    public void testGetGrant() {
//        System.out.println("getGrant");
//        OAuthClient instance = null;
//        GrantType expResult = null;
//        GrantType result = instance.getGrant();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getToken method, of class OAuthClient.
     */
    @Test
    public void testGetTokenBasicAuth() throws Exception {
        System.out.println("getToken");
        
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
        
        when(provider.getResponse((List<NameValuePair>)(List<?>)anyList(), (List<NameValuePair>)(List<?>)anyList(), (UsernamePasswordCredentials)any())).thenReturn(dummyResponse);

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
        assertEquals(expResult,result);
  

    }
    
    @Test
    public void testGetTokenClientCredentials() throws Exception {
        System.out.println("getToken");
        
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
        
        when(provider.getResponse((List<NameValuePair>)(List<?>)anyList(), (List<NameValuePair>)(List<?>)anyList(), (UsernamePasswordCredentials)any())).thenReturn(dummyResponse);

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
        assertEquals(expResult,result);
  

    }
    
    @Test
    public void testGetTokenwithScope() throws Exception {
        System.out.println("getToken");
        
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
        
        when(provider.getResponse((List<NameValuePair>)(List<?>)anyList(), (List<NameValuePair>)(List<?>)anyList(), (UsernamePasswordCredentials)any())).thenReturn(dummyResponse);

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
        assertEquals(expResult,result);
  

    }

//    /**
//     * Test of refreshToken method, of class OAuthClient.
//     */
//    @Test
//    public void testRefreshToken() throws Exception {
//        System.out.println("refreshToken");
//        Provider provider = null;
//        String refreshToken = "";
//        OAuthClient instance = null;
//        Token expResult = null;
//        Token result = instance.refreshToken(provider, refreshToken);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
