/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author btaljaard
 */
public class ClientCredentialsGrant implements GrantType {

    private static final String GRANT_TYPE = "client_credentials";
    private static final ImmutableList<String> SUPPORTED_OPERATIONS = ImmutableList.of("authenticate");
    private static final ImmutableList<String> MINIMUM_GRANT_PARAMETERS = ImmutableList.of("grant_type");

    private static final ImmutableList<String> CLIENT_AUTH_PARAMETERS = ImmutableList.of("client_id", "client_secret");
    private static final ImmutableList<String> BASIC_AUTH_PARAMETERS = ImmutableList.of("basic_username", "basic_password");

    @Override
    public String getType() {
        return GRANT_TYPE;
    }

    @Override
    public List<String> getSupportedOperations() {
        return SUPPORTED_OPERATIONS;
    }

    @Override
    public Boolean validParameters(Map params) {
        if (Util.validParameters(params, REQUIRED_PARAMETERS) && Util.validParameters(params, MINIMUM_GRANT_PARAMETERS) && (Util.validParameters(params, CLIENT_AUTH_PARAMETERS) || Util.validParameters(params, BASIC_AUTH_PARAMETERS))) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public List<String> getMinimumGrantParameters() {
        return MINIMUM_GRANT_PARAMETERS;
    }

}
