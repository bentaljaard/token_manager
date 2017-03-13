/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
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
    
    @Test
    public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException{
        OAuthClient instance = new OAuthClient();
        final Field params = instance.getClass().getDeclaredField("params");
        params.setAccessible(true);
        assertEquals("Fields didn't match", params.get(instance), null);
    }
    
    @Test
    public void testValueConstructor() throws NoSuchFieldException, IllegalAccessException{
        Map param_values = new HashMap();
        param_values.put("testkey", "testvalue");
        OAuthClient instance = new OAuthClient(param_values);
        final Field params = instance.getClass().getDeclaredField("params");
        params.setAccessible(true);
        assertEquals("Fields didn't match", params.get(instance), param_values);
    }

    /**
     * Test of setParams method, of class OAuthClient.
     */
    @Test
    public void testSetParams() throws IllegalAccessException, NoSuchFieldException {

        Map param_values = new HashMap();
        param_values.put("provider_id", "test_provider");
        OAuthClient instance = new OAuthClient();
        instance.setParams(param_values);
        
        final Field params = instance.getClass().getDeclaredField("params");
        params.setAccessible(true);
        assertEquals("Fields didn't match", params.get(instance), param_values);
        
    }

    /**
     * Test of getParams method, of class OAuthClient.
     */
    @Test
    public void testGetParams() throws IllegalAccessException, NoSuchFieldException {
        OAuthClient instance = new OAuthClient();
        Map param_values = new HashMap();
        param_values.put("provider_id", "test_provider");
       
        final Field params = instance.getClass().getDeclaredField("params");
        params.setAccessible(true);
        params.set(instance, param_values);
        
        Map result = instance.getParams();
        
        assertEquals("Fields didn't match", result, param_values);

    }

    /**
     * Test of getToken method, of class OAuthClient.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenNoGrant() throws Exception {
        OAuthClient instance = new OAuthClient();
        Token result = instance.getToken();
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testGetTokenInvalidGrant() throws Exception {
        OAuthClient instance = new OAuthClient();
        Map params = new HashMap();
        params.put("grant_type", "test_grant");
        instance.setParams(params);
        Token result = instance.getToken();
        
    }
    
//    @Test
//    public void testGetTokenClientCredentialsGrant() throws Exception {
//        mock(ClientCredentialsGrant.class);
//        OAuthClient instance = new OAuthClient();
//        Map params = new HashMap();
//        params.put("provider_url", "http://test.com");
//        params.put("grant_type", "client_credentials");
//        params.put("client_id", "test_client");
//        params.put("provider_id", "test_provider");
//        params.put("client_secret", "test_secret");
//        instance.setParams(params);
//        Token result = instance.getToken();
//        
//    }
    
    

    /**
     * Test of refreshToken method, of class OAuthClient.
     */
//    @Test
//    public void testRefreshToken() throws Exception {
//        System.out.println("refreshToken");
//        String refreshToken = "";
//        OAuthClient instance = new OAuthClient();
//        Token expResult = null;
//        Token result = instance.refreshToken(refreshToken);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
