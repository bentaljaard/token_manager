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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author btaljaard
 */
public class ClientCredentialsGrant implements GrantType {

    private static final Logger logger = Logger.getLogger(ClientCredentialsGrant.class.getName());

    @Override
    public Token authenticate(Map params) throws Exception {
        /**
         * Expected parameters: provider_url, provider_id, grant_type,client_id
         * Optional parameters: client_secret, username, password
         */

        Boolean useBasicAuth = false;
        Boolean useClientCredentials = false;

        logger.log(Level.INFO, "Received the following parameters {0}", params.toString());

        //Check that we recieved the minimum expected parameters
        List<String> expected = new ArrayList<String>();
        expected.add("provider_url");
        expected.add("grant_type");
        expected.add("client_id");
        expected.add("provider_id");
        if (!Util.validParameters(params, expected)) {
            logger.log(Level.SEVERE, "Received the following parameters {0}", params.toString());
            throw new IllegalArgumentException("All the required parameters for a client credential grant was not specified");
        }

        // Can either authenticate using client_id and client_secret or with basic authentication
        // Prefer basic auth (do we need both?)
        if (!(params.containsKey("basic_username") && params.containsKey("basic_password"))) {
            if (!(params.containsKey("client_id") && params.containsKey("client_secret"))) {
                logger.log(Level.SEVERE, "No authentication details were provided");
                logger.log(Level.SEVERE, "Received the following parameters {0}", params.toString());
                throw new IllegalArgumentException("client_id and client_secret or basic auth credentials are required for this grant type");
            } else {
                useClientCredentials = true;
            }

        } else {
            useBasicAuth = true;
        }
        
        HTTPRequest client = new HTTPRequest();
        UsernamePasswordCredentials credentials = null;
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        
        if (useBasicAuth){
            credentials = new UsernamePasswordCredentials((String) params.get("basic_username"), (String) params.get("basic_password"));
        }
        
        //Set grant request parameters
        urlParameters.add(new BasicNameValuePair("grant_type", (String) params.get("grant_type")));

        if (useClientCredentials) {
            urlParameters.add(new BasicNameValuePair("client_id", (String) params.get("client_id")));
            urlParameters.add(new BasicNameValuePair("client_secret", (String) params.get("client_secret")));
        }
        
         if (params.containsKey("scope")) {
            urlParameters.add(new BasicNameValuePair("scope", (String) params.get("scope")));
        }
        
        String clientResponse = client.post((String) params.get("provider_url"), headers, urlParameters, credentials);
 
        // Save response from provider
        HashMap providerResponse = (HashMap) Util.jsonToMap(clientResponse);

        //Check if there was an error in the response
        if (providerResponse.containsKey("error")) {
            throw new AuthenticationException((String) providerResponse.get("error"));
        }

        // Set token details
        Token token = new Token();
        token.setClientID((String) params.get("client_id"));
        token.setProviderID((String)params.get("provider_id"));
//        token.setScope((String) providerResponse.get("scope"));
        token.setScope((String)params.get("scope"));
        token.setTokenType("access_token");

        //TODO: What if no expires_in on the returned access token?
        token.setTTL(Integer.parseInt((String) providerResponse.get("expires_in")));
        token.setProviderResponse(providerResponse);

        return token;

    }

    @Override
    public Token refresh(Map params, String refreshToken) throws Exception {
        throw new UnsupportedOperationException("The client_credentials grant does not support using refresh tokens");
        
    }

}
