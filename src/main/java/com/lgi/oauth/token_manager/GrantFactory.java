/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import static com.lgi.oauth.token_manager.GrantType.SUPPORTED_GRANTS;

/**
 *
 * @author btaljaard
 */
public class GrantFactory {

    public GrantType getGrant(String grantType) {
        if (grantType == null) {
            return null;
        }
        GrantType grant = null;
        if (SUPPORTED_GRANTS.contains(grantType)) {
            if (grantType.equalsIgnoreCase("CLIENT_CREDENTIALS")) {
                grant = new ClientCredentialsGrant();

            } else if (grantType.equalsIgnoreCase("PASSWORD")) {
                grant =  new PasswordGrant();

            }

        } else {
            throw new UnsupportedOperationException("Grant type is not supported");
        }

        return grant;
    }

}
