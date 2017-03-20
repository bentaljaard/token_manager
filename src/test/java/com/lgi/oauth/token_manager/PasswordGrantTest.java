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
public class PasswordGrantTest {

    public PasswordGrantTest() {
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
     * Test of getType method, of class PasswordGrant.
     */
    @Test
    public void testGetType() {
        PasswordGrant instance = new PasswordGrant();
        String expResult = "password";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSupportedOperations method, of class PasswordGrant.
     */
    @Test
    public void testGetSupportedOperations() {
        PasswordGrant instance = new PasswordGrant();
        List<String> expResult = new ArrayList<String>();
        expResult.add("authenticate");
        expResult.add("refresh");
        List<String> result = instance.getSupportedOperations();
        assertEquals(expResult, result);
    }

    /**
     * Test of validParameters method, of class PasswordGrant.
     */
    @Test
    public void testValidParameters() {
       Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        params.put("username", "test_client_1");
        params.put("password", "test_secret");
        PasswordGrant instance = new PasswordGrant();
        Boolean expResult = true;
        Boolean result = instance.validParameters(params);
        assertEquals(expResult, result);
      
    }
    
     @Test
    public void testValidParametersFalse() {
       Map params = new HashMap();
        params.put("provider_url", "http://localhost:8080/v1/oauth/tokens");
        params.put("grant_type", "password");
//        params.put("client_id", "test_client_1");
        params.put("provider_id", "local");
        params.put("basic_username", "test_client_1");
        params.put("basic_password", "test_secret");
        params.put("username", "test_client_1");
        params.put("password", "test_secret");
        PasswordGrant instance = new PasswordGrant();
       
        assertEquals(false, instance.validParameters(params));
      
    }

    /**
     * Test of getMinimumGrantParameters method, of class PasswordGrant.
     */
    @Test
    public void testGetMinimumGrantParameters() {
        PasswordGrant instance = new PasswordGrant();
        List<String> expResult = new ArrayList<String>();
        expResult.add("grant_type");
        expResult.add("username");
        expResult.add("password");
        List<String> result = instance.getMinimumGrantParameters();
        assertEquals(expResult, result);
    }

}
