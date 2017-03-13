/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author btaljaard
 */
public final class Util {
    
   
     public static Map jsonToMap(String jsonString){
        HashMap<String,String> map = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, String>>(){}.getType());
        return map;
    }
     
    public static Boolean validParameters(Map params, List <String> expected){
        Boolean valid = false;
        if((params == null || params.isEmpty()) && !expected.isEmpty()){
            return false;
        }
        
        for (String item: expected){
            if(!params.containsKey(item)){
                return false;
            } else {
                valid = true;
            }
        }
        return valid;
    }
    
}
