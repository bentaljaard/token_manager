/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
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

//    request ;
//    response ;

    public Provider(String url, String providerID) {
        this.url = url;
        this.providerID = providerID;
    }

    public String getURL() {
        return this.url;
    }

    public String getID() {
        return this.providerID;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setID(String id) {
        this.providerID = id;
    }

    public Map getResponse(List<NameValuePair> headers, List<NameValuePair> urlParameters, UsernamePasswordCredentials credentials) throws AuthenticationException, IOException {
        // Create HTTP client
        HttpClient client = HttpClientBuilder.create()
                .build();

        HttpPost post = new HttpPost(url);

        //set all headers
        post.setHeader("User-Agent", USER_AGENT);

        for (NameValuePair header : headers) {
            post.addHeader(header.getName(), header.getValue());
        }

        //Set basic auth credentials if required
        if (credentials != null) {
            post.addHeader(new BasicScheme().authenticate(credentials, post, null));
        }

        //set urlencoded post parameters
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }

        // Call provider
        HttpResponse response;

        try {
            logger.log(Level.INFO, "Sending request to {0}", url);
            response = client.execute(post);
            logger.log(Level.INFO, "Got response code: {0} [{1}]", new Object[]{response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()});
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }

        // Read the response
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = null;

        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }

        logger.log(Level.INFO, "Received the following result {0}", result.toString());
        
        return Util.jsonToMap(result.toString());

    }

}
