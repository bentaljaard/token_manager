/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

/**
 *
 * @author btaljaard
 */
public class GrantFactory {
    
   public GrantType getGrant(String grantType){
      if(grantType == null){
         return null;
      }		
      if(grantType.equalsIgnoreCase("CLIENT_CREDENTIALS")){
         return new ClientCredentialsGrant();
         
      } else if(grantType.equalsIgnoreCase("PASSWORD")){
         return new PasswordGrant();
         
      }
      
      return null;
   }
    
}
