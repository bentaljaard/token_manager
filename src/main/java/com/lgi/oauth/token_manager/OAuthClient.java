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
    
    
    public OAuthClient(){
        super();
    }
    
    public OAuthClient(Map params){
        this.params = params;
    }
    
    
    public void setParams(Map params){
        this.params = params;
    }
    
    
    public Map getParams(){
        return this.params;
    }
    
    public Token getToken() throws Exception{
        //todo check if this is set
        String grantType = (String)params.get("grant_type");
        Token token = null;
        
        if(grantType.equals("client_credentials")){
            GrantType grant = new ClientCredentialsGrant();
            token = grant.authenticate(params);
        }
        
        if(grantType.equals("password")){
            GrantType grant = new PasswordGrant();
            token = grant.authenticate(params);
        }
        return token;
    }
    
    public Token refreshToken(String refreshToken) throws Exception{
        
        String grantType = (String)params.get("grant_type");
        Token responseToken = null;
        
        if(grantType.equals("password")){
            GrantType grant = new PasswordGrant();
            responseToken = grant.refresh(params, refreshToken);
        }
        return responseToken;
    }
   
    
    
}
