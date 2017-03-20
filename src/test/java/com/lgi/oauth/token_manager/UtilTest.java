/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Modifier;
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
public class UtilTest {
    
    public UtilTest() {
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
     * Test of jsonToMap method, of class Util.
     */
    @Test
    public void testJsonToMap() {
        String jsonString = "{\"test_key1\":\"test_value1\",\"test_key2\":\"test_value2\"}";
                
        Map expResult = new HashMap();
        expResult.put("test_key1", "test_value1");
        expResult.put("test_key2", "test_value2");
        
        Map result = Util.jsonToMap(jsonString);
        assertEquals(expResult, result);
    
    }

    /**
     * Test of validParameters method, of class Util.
     */
    @Test
    public void testValidParametersNullExpectedNull() {
        Map params = null;
        List<String> expected = null;
        Boolean expResult = false;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    @Test
    public void testValidParametersEmptyExpectedNull() {
        Map params = new HashMap();
        List<String> expected = null;
        Boolean expResult = false;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void testValidParametersEmptyExpectedValues() {
        Map params = new HashMap();
        List<String> expected = new ArrayList<String>();
        expected.add("test_param");
        Boolean expResult = false;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void testValidParametersValuesExpectedValuesMatch() {
        Map params = new HashMap();
        params.put("test_param", "test_value");
        List<String> expected = new ArrayList<String>();
        expected.add("test_param");
        Boolean expResult = true;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void testValidParametersMoreValuesExpectedValuesMatch() {
        Map params = new HashMap();
        params.put("test_param", "test_value");
        params.put("test_param2", "test_value2");
        List<String> expected = new ArrayList<String>();
        expected.add("test_param");
        Boolean expResult = true;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void testValidParametersValuesExpectedValuesNoMatch() {
        Map params = new HashMap();
        params.put("test_param1", "test_value");
        List<String> expected = new ArrayList<String>();
        expected.add("test_param");
        Boolean expResult = false;
        Boolean result = Util.validParameters(params, expected);
        assertEquals(expResult, result);
       
    }
    
    @Test
    public void checkFinal(){
        Util util = new Util();
        assertTrue(Modifier.isFinal(util.getClass().getModifiers()));
    }
    
    
}
