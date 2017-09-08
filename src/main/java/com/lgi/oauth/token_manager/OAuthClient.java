/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class OAuthClient {

    private Map params;
    private GrantType grant;
    private static final Logger logger = Logger.getLogger(OAuthClient.class.getName());
    private static final ImmutableList<String> SUPPORTED_GRANTS = ImmutableList.of("client_credentials", "password");

    public OAuthClient(Map params, GrantType grant) {
        this.params = params;
        this.grant = grant;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public void setGrant(GrantType grant) {
        this.grant = grant;
    }

    public Map getParams() {
        return this.params;
    }

    public GrantType getGrant() {
        return this.grant;
    }

  

    private Token authenticate(Provider provider) throws AuthenticationException, IOException, UnknownHostException, SocketTimeoutException  {
        UsernamePasswordCredentials credentials = null;
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        logger.log(Level.INFO, "Received the following parameters {0}", params.toString());

        //Set minimum grant parameters
        for (String param : grant.getMinimumGrantParameters()) {
            urlParameters.add(new BasicNameValuePair(param, (String) params.get(param)));
        }

        // Can either authenticate using client_id and client_secret or with basic authentication
        // Prefer basic auth
        if (params.containsKey("basic_username")) {
            credentials = new UsernamePasswordCredentials((String) params.get("basic_username"), (String) params.get("basic_password"));

        } else {
            urlParameters.add(new BasicNameValuePair("client_id", (String) params.get("client_id")));
            urlParameters.add(new BasicNameValuePair("client_secret", (String) params.get("client_secret")));
        }

        //scope is an optional parameter for all grants        
        if (params.containsKey("scope")) {
            urlParameters.add(new BasicNameValuePair("scope", (String) params.get("scope")));
        }
        
        //check for custom parameters to set
        for(Object key : params.keySet()){
            
            String stringKey = (String)key;
            if(stringKey.startsWith("custom_")){
                urlParameters.add(new BasicNameValuePair(stringKey.split("custom_")[1], (String) params.get(stringKey)));
            }
        }

        logger.log(Level.INFO, "Sending the following parameters {0}", urlParameters.toString());
        // Call provider to get token
        Map providerResponse = provider.getResponse(headers, urlParameters, credentials);

        Token token = new Token();
        token.setClientID((String) params.get("client_id"));
        token.setProviderID(provider.getID());
        token.setScope((String) params.get("scope"));
        token.setProviderResponse(providerResponse);

        //Check if there was an error in the response
        if (providerResponse.containsKey("error")) {
            token.setTokenType("error_token");
            return token;
        }

        token.setTokenType("access_token");
        //If no expires_in returned in provider response, use access_token_ttl value from request
        if (providerResponse.containsKey("expires_in")) {
            token.setTTL(Long.parseLong((String) providerResponse.get("expires_in")));
        } else {
            if (params.containsKey("access_token_ttl")) {
                token.setTTL(Integer.parseInt((String) params.get("access_token_ttl")));
            } else {
                throw new IllegalArgumentException("Please set access_token_ttl parameter to determine access token TTL");
            }

        }
        logger.log(Level.FINE, "Authenticated with the provider");
        return token;
    }

    public Token getToken(Provider provider) throws AuthenticationException, IOException, UnknownHostException, SocketTimeoutException  {
        Token token = null;
        //get the type of grant
        String grantType = grant.getType();
        if (SUPPORTED_GRANTS.contains(grantType)) {
            if (grant.getSupportedOperations().contains("authenticate")) {
                if (grant.validParameters(params)) {
                    //Do authentication here
                    token = authenticate(provider);
                } else {
                    throw new IllegalArgumentException("The required parameters for this grant operation was not specified");
                }
            } else {
                throw new UnsupportedOperationException("The requested operation is not supported for this grant type");
            }
        } else {
            throw new UnsupportedOperationException("The requested grant is not supported");
        }
        return token;
    }

    private Token refresh(Provider provider, String refreshToken) throws AuthenticationException, IOException, UnknownHostException, SocketTimeoutException {
        UsernamePasswordCredentials credentials = null;
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        logger.log(Level.FINEST, "Received the following parameters {0}", params.toString());

        // Add parameters for refresh grant
        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));

        // Can either authenticate using client_id and client_secret or with basic authentication
        // Prefer basic auth
        if (params.containsKey("basic_username")) {
            credentials = new UsernamePasswordCredentials((String) params.get("basic_username"), (String) params.get("basic_password"));

        } else {
            urlParameters.add(new BasicNameValuePair("client_id", (String) params.get("client_id")));
            urlParameters.add(new BasicNameValuePair("client_secret", (String) params.get("client_secret")));
        }

        //scope is an optional parameter for all grants        
        if (params.containsKey("scope")) {
            urlParameters.add(new BasicNameValuePair("scope", (String) params.get("scope")));
        }

        // Call provider to get token
        Map providerResponse = provider.getResponse(headers, urlParameters, credentials);


        Token token = new Token();
        token.setClientID((String) params.get("client_id"));
        token.setProviderID(provider.getID());
        token.setScope((String) params.get("scope"));
        token.setProviderResponse(providerResponse);

        //Check if there was an error in the response
        if (providerResponse.containsKey("error")) {
            token.setTokenType("error_token");
            return token;
        }

        token.setTokenType("access_token");
        //If no expires_in returned in provider response, use access_token_ttl value from request
        if (providerResponse.containsKey("expires_in")) {
            token.setTTL(Integer.parseInt((String) providerResponse.get("expires_in")));
        } else {
            if (params.containsKey("access_token_ttl")) {
                token.setTTL(Integer.parseInt((String) params.get("access_token_ttl")));
            } else {
                throw new IllegalArgumentException("Please set access_token_ttl parameter to determine access token TTL");
            }

        }
        logger.log(Level.FINE, "Authenticated with the provider using refresh token");
        return token;
    }

    public Token refreshToken(Provider provider, String refreshToken) throws AuthenticationException, IOException, UnknownHostException, SocketTimeoutException  {

        Token token = null;
        //get the type of grant
        String grantType = grant.getType();
        if (SUPPORTED_GRANTS.contains(grantType)) {
            if (grant.getSupportedOperations().contains("refresh")) {
                if (grant.validParameters(params)) {
                    //Do authentication here
                    token = refresh(provider, refreshToken);
                } else {
                    throw new IllegalArgumentException("The required parameters for this grant operation was not specified");
                }
            } else {
                throw new UnsupportedOperationException("The requested operation is not supported for this grant type");
            }
        } else {
            throw new UnsupportedOperationException("The requested grant is not supported");
        }
        return token;
    }

}
