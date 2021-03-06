/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;

/**
 *
 * @author btaljaard
 */
public class ProviderTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private MockServerClient mockServerClient;

    public ProviderTest() {
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
     * Test of getURL method, of class Provider.
     */
    @Test
    public void testGetURL() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Provider instance = new Provider("http://test.com",10,10);
        String expResult = "http://test.com";
        String result = instance.getURL();
        assertEquals(expResult, result);

    }

    /**
     * Test of getID method, of class Provider.
     */
    @Test
    public void testGetID() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Provider instance = new Provider("http://test.com",10,10);

        byte[] bytesOfMessage = "http://test.com".getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String expResult = new BigInteger(1, md.digest(bytesOfMessage)).toString(16);

        String result = instance.getID();
        assertEquals(expResult, result);

    }

    /**
     * Test of setURL method, of class Provider.
     */
    @Test
    public void testSetURL() throws NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String url = "http://test.com";
        Provider instance = new Provider(null,10,10);
        instance.setURL(url);

        final Field url_value = instance.getClass().getDeclaredField("url");
        url_value.setAccessible(true);
        assertEquals("Fields didn't match", url_value.get(instance), url);

    }
    
     @Test
    public void testSetURLNull() throws NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String url = null;
        Provider instance = new Provider(null,10,10);
        instance.setURL(url);

        final Field url_value = instance.getClass().getDeclaredField("url");
        url_value.setAccessible(true);
        assertEquals("Fields didn't match", url_value.get(instance), url);

    }
    
    @Test
    public void testSetConnectTimeout() throws NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        int timeout = 60;
        Provider instance = new Provider(null,10,10);
        instance.setConnectTimeout(timeout);

        final Field timeout_value = instance.getClass().getDeclaredField("connectTimeoutMS");
        timeout_value.setAccessible(true);
        assertEquals("Fields didn't match", timeout_value.get(instance), timeout);

    }
    
    @Test
    public void testSetSocketTimeout() throws NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        int timeout = 60;
        Provider instance = new Provider(null,10,10);
        instance.setSocketTimeout(timeout);

        final Field timeout_value = instance.getClass().getDeclaredField("socketTimeoutMS");
        timeout_value.setAccessible(true);
        assertEquals("Fields didn't match", timeout_value.get(instance), timeout);

    }

//    /**
//     * Test of setID method, of class Provider.
//     */
//    @Test
//    public void testSetID() throws NoSuchFieldException, IllegalAccessException {
//        String id = "test";
//        Provider instance = new Provider(null, null);
//        instance.setID(id);
//        final Field id_value = instance.getClass().getDeclaredField("providerID");
//        id_value.setAccessible(true);
//        assertEquals("Fields didn't match", id_value.get(instance), id);
//    }
    /**
     * Test of getResponse method, of class Provider.
     */
    @Test
    public void testGetResponseNoJSON() throws Exception {
        mockServerClient.when(HttpRequest.request("/test")).respond(HttpResponse.response()
                .withStatusCode(200).withHeader("Content-type", "text/plain"));

        System.out.println("**********************************");
        System.out.println(mockServerRule.getPort());
        System.out.println("**********************************");

        String url = "http://localhost:" + mockServerRule.getPort() + "/test";

        Provider instance = new Provider(url,10,-1);

        List<NameValuePair> headers = null;
        List<NameValuePair> urlParameters = null;
        UsernamePasswordCredentials credentials = null;

        Map expResult = new HashMap();
        expResult.put("error", "Could not parse response from provider");
        Map result = instance.getResponse(headers, urlParameters, credentials);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetResponseErrorCodeNoJSON() throws Exception {
        mockServerClient.when(HttpRequest.request("/test")).respond(HttpResponse.response()
                .withStatusCode(500).withHeader("Content-type", "text/plain"));

        System.out.println("**********************************");
        System.out.println(mockServerRule.getPort());
        System.out.println("**********************************");

        String url = "http://localhost:" + mockServerRule.getPort() + "/test";

        Provider instance = new Provider(url,10,-1);

        List<NameValuePair> headers = null;
        List<NameValuePair> urlParameters = null;
        UsernamePasswordCredentials credentials = null;

        Map expResult = new HashMap();
        expResult.put("error", "Error Code:500 - Internal Server Error");
        Map result = instance.getResponse(headers, urlParameters, credentials);
        assertEquals(expResult, result);
    }

    @Test(expected = UnknownHostException.class)
    public void testGetResponseOfflineURL() throws Exception {
        mockServerClient.when(HttpRequest.request("/test")).respond(HttpResponse.response().withStatusCode(200));

        System.out.println("**********************************");
        System.out.println(mockServerRule.getPort());
        System.out.println("**********************************");

        String url = "http://blahdummy.io";

        Provider instance = new Provider(url,10,-1);

        List<NameValuePair> headers = null;
        List<NameValuePair> urlParameters = null;
        UsernamePasswordCredentials credentials = null;

        Map expResult = new HashMap();
        expResult.put("error", "Response from provider is not JSON");
        Map result = instance.getResponse(headers, urlParameters, credentials);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetResponse() throws Exception {
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        String accessToken = UUID.randomUUID().toString();

        JSONObject json = new JSONObject();
        json.put("access_token", accessToken);
        json.put("expires_in", new Long(60));
        json.put("token_type", "Bearer");
        json.put("scope", "read_write");
        mockServerClient.when(HttpRequest.request("/test")).respond(HttpResponse.response()
                .withStatusCode(200)
                .withHeader("Content-type", "application/json")
                .withBody(json.toString()));

        System.out.println("**********************************");
        System.out.println(mockServerRule.getPort());
        System.out.println("**********************************");

        String url = "http://localhost:" + mockServerRule.getPort() + "/test";

        Provider instance = new Provider(url,10,-1);

        urlParameters.add(new BasicNameValuePair("testkey", "testvalue"));
        headers.add(new BasicNameValuePair("testheader", "testheadervalue"));

        UsernamePasswordCredentials credentials = null;

        Map expResult = new HashMap();
        expResult.put("access_token", accessToken);
        expResult.put("expires_in", "60");
        expResult.put("token_type", "Bearer");
        expResult.put("scope", "read_write");
        Map result = instance.getResponse(headers, urlParameters, credentials);
        assertEquals(expResult, result);

        mockServerClient.verify(HttpRequest.request()
                .withHeader("testheader", "testheadervalue")
                .withBody(new ParameterBody(new Parameter("testkey", "testvalue"))));
    }

    @Test
    public void testGetResponseBasicAuth() throws Exception {
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        String accessToken = UUID.randomUUID().toString();

        JSONObject json = new JSONObject();
        json.put("access_token", accessToken);
        json.put("expires_in", new Long(60));
        json.put("token_type", "Bearer");
        json.put("scope", "read_write");
        mockServerClient.when(HttpRequest.request("/test")).respond(HttpResponse.response()
                .withStatusCode(200)
                .withHeader("Content-type", "application/json")
                .withBody(json.toString()));

        System.out.println("**********************************");
        System.out.println(mockServerRule.getPort());
        System.out.println("**********************************");

        String url = "http://localhost:" + mockServerRule.getPort() + "/test";

        Provider instance = new Provider(url,10,-1);

        urlParameters.add(new BasicNameValuePair("testkey", "testvalue"));
        headers.add(new BasicNameValuePair("testheader", "testheadervalue"));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test_user", "test_password");

        Map expResult = new HashMap();
        expResult.put("access_token", accessToken);
        expResult.put("expires_in", "60");
        expResult.put("token_type", "Bearer");
        expResult.put("scope", "read_write");
        Map result = instance.getResponse(headers, urlParameters, credentials);
        assertEquals(expResult, result);
        mockServerClient.verify(HttpRequest.request()
                .withHeader("testheader", "testheadervalue")
                .withBody(new ParameterBody(new Parameter("testkey", "testvalue")))
                .withHeader("Authorization"));
    }

}
