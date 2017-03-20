/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

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
public class GrantFactoryTest {
    
    public GrantFactoryTest() {
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
     * Test of getGrant method, of class GrantFactory.
     */
    @Test
    public void testGetGrantClientCredentials() {
        String grantType = "client_credentials";
        GrantFactory instance = new GrantFactory();
        assertTrue(instance.getGrant(grantType) instanceof ClientCredentialsGrant);
      
    }
    
    @Test
    public void testGetGrantPassword() {
       String grantType = "password";
        GrantFactory instance = new GrantFactory();
        assertTrue(instance.getGrant(grantType) instanceof PasswordGrant);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testGetGrantNotFound() {
        String grantType = "blah";
        GrantFactory instance = new GrantFactory();
        GrantType result = instance.getGrant(grantType);
       
    }
    
    @Test
    public void testGetGrantNull() {
        String grantType = null;
        GrantFactory instance = new GrantFactory();
        GrantType expResult = null;
        GrantType result = instance.getGrant(grantType);
        assertEquals(expResult, result);
       
    }
    
}
