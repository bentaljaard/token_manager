/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
public class TokenTest {

    public TokenTest() {
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
    public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
        Token token = new Token();

        //check client id is null
        final Field clientID = token.getClass().getDeclaredField("clientID");
        clientID.setAccessible(true);
        assertEquals("Fields didn't match", clientID.get(token), null);

        //check provider id is null
        final Field providerID = token.getClass().getDeclaredField("providerID");
        providerID.setAccessible(true);
        assertEquals("Fields didn't match", providerID.get(token), null);

        //check token type is null
        final Field tokenType = token.getClass().getDeclaredField("tokenType");
        tokenType.setAccessible(true);
        assertEquals("Fields didn't match", tokenType.get(token), null);

        //check token type is null
        final Field scope = token.getClass().getDeclaredField("scope");
        scope.setAccessible(true);
        assertEquals("Fields didn't match", scope.get(token), null);

        //check ttl is null
        final Field ttl = token.getClass().getDeclaredField("ttl");
        ttl.setAccessible(true);
        assertEquals("Fields didn't match", ttl.get(token), 0L);

        //check provider response is null
        final Field providerResponse = token.getClass().getDeclaredField("providerResponse");
        providerResponse.setAccessible(true);
        assertEquals("Fields didn't match", providerResponse.get(token), null);
    }

    @Test
    public void testValueConstructor() throws NoSuchFieldException, IllegalAccessException {
        String client_id = "test_client";
        String provider_id = "test_provider";
        String token_type = "test_token";
        Long time_to_live = 5L;
        String scope_value = "test_scope";
        Map provider_response = new HashMap();
        provider_response.put("test_key", "test_value");

        Token token = new Token(client_id, provider_id, scope_value, token_type, time_to_live, provider_response);

        //check client id is null
        final Field clientID = token.getClass().getDeclaredField("clientID");
        clientID.setAccessible(true);
        assertEquals("Fields didn't match", clientID.get(token), client_id);

        //check provider id is null
        final Field providerID = token.getClass().getDeclaredField("providerID");
        providerID.setAccessible(true);
        assertEquals("Fields didn't match", providerID.get(token), provider_id);

        //check token type is null
        final Field tokenType = token.getClass().getDeclaredField("tokenType");
        tokenType.setAccessible(true);
        assertEquals("Fields didn't match", tokenType.get(token), token_type);

        //check token type is null
        final Field scope = token.getClass().getDeclaredField("scope");
        scope.setAccessible(true);
        assertEquals("Fields didn't match", scope.get(token), scope_value);

        //check ttl is null
        final Field ttl = token.getClass().getDeclaredField("ttl");
        ttl.setAccessible(true);
        assertEquals("Fields didn't match", ttl.get(token), time_to_live);

        //check provider response is null
        final Field providerResponse = token.getClass().getDeclaredField("providerResponse");
        providerResponse.setAccessible(true);
        assertEquals("Fields didn't match", providerResponse.get(token), provider_response);
    }

    /**
     * Test of getClientID method, of class Token.
     */
    @Test
    public void testGetClientID() throws IllegalAccessException, NoSuchFieldException {
        String client_id = "test_client";
        Token instance = new Token();
        final Field clientID = instance.getClass().getDeclaredField("clientID");
        clientID.setAccessible(true);
        clientID.set(instance, client_id);
        assertEquals("Fields didn't match", instance.getClientID(), client_id);
    }

    /**
     * Test of getProviderID method, of class Token.
     */
    @Test
    public void testGetProviderID() throws IllegalAccessException, NoSuchFieldException {
        String provider_id = "test_provider";
        Token instance = new Token();
        final Field providerID = instance.getClass().getDeclaredField("providerID");
        providerID.setAccessible(true);
        providerID.set(instance, provider_id);
        assertEquals("Fields didn't match", instance.getProviderID(), provider_id);
    }

    /**
     * Test of getScope method, of class Token.
     */
    @Test
    public void testGetScope() throws NoSuchFieldException, IllegalAccessException {
        String scope_value = "test_scope";
        Token instance = new Token();
        final Field scope = instance.getClass().getDeclaredField("scope");
        scope.setAccessible(true);
        scope.set(instance, scope_value);
        assertEquals("Fields didn't match", instance.getScope(), scope_value);
    }

    /**
     * Test of getTokenType method, of class Token.
     */
    @Test
    public void testGetTokenType() throws NoSuchFieldException, IllegalAccessException {
        String token_type = "test_token";
        Token instance = new Token();
        final Field tokenType = instance.getClass().getDeclaredField("tokenType");
        tokenType.setAccessible(true);
        tokenType.set(instance, token_type);
        assertEquals("Fields didn't match", instance.getTokenType(), token_type);
    }

    /**
     * Test of getTTL method, of class Token.
     */
    @Test
    public void testGetTTL() throws IllegalAccessException, NoSuchFieldException {
        Long time_to_live = 5L;
        Token instance = new Token();
        final Field ttl = instance.getClass().getDeclaredField("ttl");
        ttl.setAccessible(true);
        ttl.set(instance, time_to_live);
        assertEquals("Fields didn't match", (Long) instance.getTTL(), time_to_live);
    }

    /**
     * Test of getProviderResponse method, of class Token.
     */
    @Test
    public void testGetProviderResponse() throws NoSuchFieldException, IllegalAccessException {
        Map provider_response = new HashMap();
        provider_response.put("testkey", "testvalue");
        Token instance = new Token();
        final Field providerResponse = instance.getClass().getDeclaredField("providerResponse");
        providerResponse.setAccessible(true);
        providerResponse.set(instance, provider_response);
        assertEquals("Fields didn't match", instance.getProviderResponse(), provider_response);
    }

    /**
     * Test of setClientID method, of class Token.
     */
    @Test
    public void testSetClientID() throws NoSuchFieldException, IllegalAccessException {
        String client_id = "test_client";
        Token instance = new Token();
        instance.setClientID(client_id);

        final Field clientID = instance.getClass().getDeclaredField("clientID");
        clientID.setAccessible(true);
        assertEquals("Fields didn't match", clientID.get(instance), client_id);
    }

    /**
     * Test of setProviderID method, of class Token.
     */
    @Test
    public void testSetProviderID() throws IllegalAccessException, NoSuchFieldException {
        String provider_id = "test_provider";
        Token instance = new Token();
        instance.setProviderID(provider_id);

        final Field providerID = instance.getClass().getDeclaredField("providerID");
        providerID.setAccessible(true);
        assertEquals("Fields didn't match", providerID.get(instance), provider_id);
    }

    /**
     * Test of setScope method, of class Token.
     */
    @Test
    public void testSetScope() throws IllegalAccessException, NoSuchFieldException {
        String scope_value = "test_scope";
        Token instance = new Token();
        instance.setScope(scope_value);

        final Field scope = instance.getClass().getDeclaredField("scope");
        scope.setAccessible(true);
        assertEquals("Fields didn't match", scope.get(instance), scope_value);
    }

    /**
     * Test of setTokenType method, of class Token.
     */
    @Test
    public void testSetTokenType() throws IllegalAccessException, NoSuchFieldException {
        String token_type = "test_token";
        Token instance = new Token();
        instance.setTokenType(token_type);

        final Field tokenType = instance.getClass().getDeclaredField("tokenType");
        tokenType.setAccessible(true);
        assertEquals("Fields didn't match", tokenType.get(instance), token_type);
    }

    /**
     * Test of setTTL method, of class Token.
     */
    @Test
    public void testSetTTL() throws IllegalAccessException, NoSuchFieldException {
        long time_to_live = 5L;
        Token instance = new Token();
        instance.setTTL(time_to_live);

        final Field ttl = instance.getClass().getDeclaredField("ttl");
        ttl.setAccessible(true);
        assertEquals("Fields didn't match", ttl.get(instance), time_to_live);
    }

    /**
     * Test of setProviderResponse method, of class Token.
     */
    @Test
    public void testSetProviderResponse() throws IllegalAccessException, NoSuchFieldException {
        Map provider_response = new HashMap();
        provider_response.put("testkey", "testvalue");

        Token instance = new Token();
        instance.setProviderResponse(provider_response);

        final Field providerResponse = instance.getClass().getDeclaredField("providerResponse");
        providerResponse.setAccessible(true);
        assertEquals("Fields didn't match", providerResponse.get(instance), provider_response);
    }

    /**
     * Test of toString method, of class Token.
     */
    @Test
    public void testToStringEmpty() {
        Token instance = new Token();
        String expResult = "clientID: null, providerID: null, scope: null, tokenType: null, ttl: 0, providerResponse: null";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

    @Test
    public void testToString() {
        String client_id = "test_client";
        String provider_id = "test_provider";
        String token_type = "test_token";
        Long time_to_live = 5L;
        String scope_value = "test_scope";
        Map provider_response = new HashMap();
        provider_response.put("test_key", "test_value");

        Token instance = new Token(client_id, provider_id, scope_value, token_type, time_to_live, provider_response);
        String expResult = "clientID: test_client, providerID: test_provider, scope: test_scope, tokenType: test_token, ttl: 5, providerResponse: {test_key=test_value}";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

    @Test
    public void testGetTokenCacheKeyPrefix() {
        String client_id = "test_client";
        String provider_id = "test_provider";
        String token_type = "test_token";
        Long time_to_live = 5L;
        String scope_value = "test_scope";
        Map provider_response = new HashMap();
        provider_response.put("test_key", "test_value");

        Token token = new Token(client_id, provider_id, scope_value, token_type, time_to_live, provider_response);

        assertEquals("test_provider|test_client|test_scope", token.getTokenCacheKeyPrefix());

    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Token.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}
