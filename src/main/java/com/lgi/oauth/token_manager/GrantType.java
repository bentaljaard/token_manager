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
public interface GrantType {
    public Token authenticate(Map params) throws Exception;
    
    public Token refresh(Map params, String refreshToken) throws Exception;
    
}
