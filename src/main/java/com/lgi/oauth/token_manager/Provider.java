/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author btaljaard
 */
public class Provider {

    private String url;
    private String providerID;
    private static final Logger logger = Logger.getLogger(Provider.class.getName());
    private final String USER_AGENT = "Mozilla/5.0";
    public static final ImmutableList<Integer> SUCCESS_CODES = ImmutableList.of(200, 201, 204);
    private int connectTimeoutMS;
    private int socketTimeoutMS;

    public Provider(String url, int connectTimeoutMS, int socketTimeoutMS) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.url = url;
        if (url == null) {
            this.providerID = null;
        } else {
            this.providerID = calculateProviderHash(url);
        }
        this.connectTimeoutMS = connectTimeoutMS;
        this.socketTimeoutMS = socketTimeoutMS;

    }

    private String calculateProviderHash(String url) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = url.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String providerID = new BigInteger(1, md.digest(bytesOfMessage)).toString(16);
        return providerID;
    }

    public String getURL() {
        return this.url;
    }

    public String getID() {
        return this.providerID;
    }

    public void setURL(String url) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.url = url;
        if (url == null) {
            this.providerID = null;
        } else {
            this.providerID = calculateProviderHash(url);
        }
    }

    public void setConnectTimeout(int ms) {
        this.connectTimeoutMS = ms;
    }

    public void setSocketTimeout(int ms) {
        this.socketTimeoutMS = ms;
    }

//    public void setID(String id) {
//        this.providerID = id;
//    }
    public Map getResponse(List<NameValuePair> headers, List<NameValuePair> urlParameters, UsernamePasswordCredentials credentials) throws AuthenticationException, IOException, UnknownHostException, SocketTimeoutException {
        // Create HTTP client
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectTimeout(connectTimeoutMS)
                .setSocketTimeout(socketTimeoutMS).build();

        HttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();

        HttpPost post = new HttpPost(url);

        //set all headers
        post.setHeader("User-Agent", USER_AGENT);

        if (headers != null) {
            for (NameValuePair header : headers) {
                post.addHeader(header.getName(), header.getValue());
            }
        }

        //Set basic auth credentials if required
        if (credentials != null) {
            post.addHeader(new BasicScheme().authenticate(credentials, post, null));
        }

        //set urlencoded post parameters
        if (urlParameters != null) {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        }

        // Call provider
        HttpResponse response;

        logger.log(Level.FINE, "Sending request to {0}", url);
        response = client.execute(post);
        logger.log(Level.FINE, "Got response code: {0} [{1}]", new Object[]{response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()});

        // Read the response
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = null;

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        logger.log(Level.FINEST, "Received the following result {0}", result.toString());

        Header contentType = response.getFirstHeader("Content-Type");
        String mimeType = "";
        if (contentType != null) {
            mimeType = contentType.getValue().split(";")[0].trim();
        }

        Map responseBody = null;
        if (mimeType.equals("application/json")) {
            responseBody = Util.jsonToMap(result.toString());
        } else {
            if (!SUCCESS_CODES.contains(response.getStatusLine().getStatusCode())) {
                responseBody = Util.jsonToMap("{\"error\":\"Error Code:" + response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase() + "\"}");
            } else {
                responseBody = Util.jsonToMap("{\"error\":\"Could not parse response from provider\"}");

            }
        }
        return responseBody;
    }
}
