/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import java.util.Map;

/**
 *
 * @author btaljaard
 */
public class OAuthClient {

    private Map params;

    private enum SupportedGrants {
        client_credentials, password
    }

    public OAuthClient() {
        super();
    }

    public OAuthClient(Map params) {
        this.params = params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getParams() {
        return this.params;
    }

    public Token getToken() throws Exception {
        String grantType;
        Token token = null;
        GrantType grant = null;

        if (params != null && params.containsKey("grant_type")) {
            grantType = (String) params.get("grant_type");
        } else {
            throw new IllegalArgumentException("The grant type needs to be specified when requesting a token");
        }

//        try {
            switch (SupportedGrants.valueOf(grantType)) {
                case client_credentials:
                    grant = new ClientCredentialsGrant();
                    token = grant.authenticate(params);
                    break;
                case password:
                    grant = new PasswordGrant();
                    token = grant.authenticate(params);
                    break;
                default:
                    throw new UnsupportedOperationException("The requested grant is not supported");
            }
//        } catch (IllegalArgumentException e) {
//            throw new UnsupportedOperationException("The requested grant is not supported");
//        }

        return token;
    }

    public Token refreshToken(String refreshToken) throws Exception {
        String grantType;
        Token responseToken = null;
        GrantType grant = null;

        if (params != null && params.containsKey("grant_type")) {
            grantType = (String) params.get("grant_type");
        } else {
            throw new IllegalArgumentException("The grant type needs to be specified when requesting a token");
        }

        switch (SupportedGrants.valueOf(grantType)) {
            case password:
                grant = new PasswordGrant();
                responseToken = grant.refresh(params, refreshToken);
                break;
            default:
                throw new UnsupportedOperationException("The grant type does not support refresh");
        }
        return responseToken;
    }

}
