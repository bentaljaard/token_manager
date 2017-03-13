/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.util.HashMap;
import java.util.Map;
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
public class TokenManagerIT {

    public TokenManagerIT() {
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
        System.out.println("getTokenCache");
        TokenManager instance = new TokenManager();
        TokenCache expResult = null;
        TokenCache result = instance.getTokenCache();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearTokenCache method, of class TokenManager.
     */
    @Test
    public void testClearTokenCache() {
        System.out.println("clearTokenCache");
        TokenManager instance = new TokenManager();
        instance.clearTokenCache();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBearerToken method, of class TokenManager.
     */
    @Test
    public void testGetBearerToken() {
        System.out.println("getBearerToken");
        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        
        TokenManager instance = new TokenManager();
        Token expResult = new Token("test_client_1", "local", null, "access_token", 3600, null);
        Token result = instance.getBearerToken(params);
        System.out.println(result.toString());
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}
