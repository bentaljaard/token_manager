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
import java.util.ArrayList;
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
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author btaljaard
 */
public class OAuthClient {

    private Map params;
    private GrantType grant;
    private final String USER_AGENT = "Mozilla/5.0";
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

    private String providerRequest(String url, List<NameValuePair> headers, List<NameValuePair> urlParameters, UsernamePasswordCredentials credentials) throws AuthenticationException, IOException {
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
        return result.toString();

    }

    private Token authenticate(Provider provider) throws AuthenticationException, IOException {
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

        // Call provider to get token
        //String clientResponse = providerRequest((String) params.get("provider_url"), headers, urlParameters, credentials);
        Map providerResponse = provider.getResponse(headers, urlParameters, credentials);

        // Save response from provider
//        Map providerResponse = (HashMap) Util.jsonToMap(clientResponse);
        Token token = new Token();
        token.setClientID((String) params.get("client_id"));
        token.setProviderID((String) params.get("provider_id"));
        token.setScope((String) params.get("scope"));
        token.setProviderResponse(providerResponse);

        //Check if there was an error in the response
        if (providerResponse.containsKey("error")) {
            token.setTokenType("error_token");
            return token;
        }

        token.setTokenType("access_token");
        //If no expires_in returned in provider response, use session_duration value from request
        if (providerResponse.containsKey("expires_in")) {
            token.setTTL(Long.parseLong((String) providerResponse.get("expires_in")));
        } else {
            if (params.containsKey("session_duration")) {
                token.setTTL(Integer.parseInt((String) params.get("session_duration")));
            } else {
                throw new IllegalArgumentException("Please set session_duration parameter to determine access token TTL");
            }

        }
        return token;
    }

    public Token getToken(Provider provider) throws AuthenticationException, IOException {
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

    private Token refresh(Provider provider, String refreshToken) throws AuthenticationException, IOException {
        UsernamePasswordCredentials credentials = null;
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        logger.log(Level.INFO, "Received the following parameters {0}", params.toString());

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
//        String clientResponse = providerRequest((String) params.get("provider_url"), headers, urlParameters, credentials);
        Map providerResponse = provider.getResponse(headers, urlParameters, credentials);

        // Save response from provider
//        Map providerResponse = (HashMap) Util.jsonToMap(clientResponse);

        Token token = new Token();
        token.setClientID((String) params.get("client_id"));
        token.setProviderID((String) params.get("provider_id"));
        token.setScope((String) params.get("scope"));
        token.setProviderResponse(providerResponse);

        //Check if there was an error in the response
        if (providerResponse.containsKey("error")) {
            token.setTokenType("error_token");
            return token;
        }

        token.setTokenType("access_token");
        //If no expires_in returned in provider response, use session_duration value from request
        if (providerResponse.containsKey("expires_in")) {
            token.setTTL(Integer.parseInt((String) providerResponse.get("expires_in")));
        } else {
            if (params.containsKey("session_duration")) {
                token.setTTL(Integer.parseInt((String) params.get("session_duration")));
            } else {
                throw new IllegalArgumentException("Please set session_duration parameter to determine access token TTL");
            }

        }
        return token;
    }

    public Token refreshToken(Provider provider, String refreshToken) throws AuthenticationException, IOException {

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
