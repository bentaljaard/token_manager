
package com.lgi.oauth.token_manager;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author btaljaard
 */
public interface GrantType {
    
    public static final ImmutableList<String> REQUIRED_PARAMETERS = ImmutableList.of("provider_url", "provider_id", "grant_type", "client_id");
    public static final ImmutableList<String> SUPPORTED_GRANTS = ImmutableList.of("client_credentials", "password");
    
    public String getType();
    
    public List<String> getSupportedOperations();
    
    public Boolean validParameters(Map params);
    
    public List<String> getMinimumGrantParameters();
    
}
