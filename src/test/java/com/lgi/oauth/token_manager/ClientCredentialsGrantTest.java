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
public class ClientCredentialsGrantTest {

    public ClientCredentialsGrantTest() {
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
     * Test of getType method, of class ClientCredentialsGrant.
     */
    @Test
    public void testGetType() {
        ClientCredentialsGrant instance = new ClientCredentialsGrant();
        String expResult = "client_credentials";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSupportedOperations method, of class ClientCredentialsGrant.
     */
    @Test
    public void testGetSupportedOperations() {
        ClientCredentialsGrant instance = new ClientCredentialsGrant();
        List<String> expResult = new ArrayList<String>();
        expResult.add("authenticate");
        List<String> result = instance.getSupportedOperations();
        assertEquals(expResult, result);
    }

    /**
     * Test of validParameters method, of class ClientCredentialsGrant.
     */
    @Test
    public void testValidParameters() {
        Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("client_secret", "test_secret");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");

        ClientCredentialsGrant instance = new ClientCredentialsGrant();
        Boolean expResult = true;
        Boolean result = instance.validParameters(params);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of getMinimumGrantParameters method, of class
     * ClientCredentialsGrant.
     */
    @Test
    public void testGetMinimumGrantParameters() {
        ClientCredentialsGrant instance = new ClientCredentialsGrant();
        List<String> expResult = new ArrayList<String>();
        expResult.add("grant_type");
        List<String> result = instance.getMinimumGrantParameters();
        assertEquals(expResult, result);

    }

}
