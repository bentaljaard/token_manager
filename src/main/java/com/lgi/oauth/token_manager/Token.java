package com.lgi.oauth.token_manager;

import java.util.Map;

public class Token {

    private String clientID;
    private String tokenType;
    private String providerID;
    private String scope;
    private long ttl;
    private Map providerResponse;

    public Token() {
        super();
        clientID = null;
        tokenType = null;
        providerID = null;
        scope = null;
        ttl = 0L;
        providerResponse = null;
    }

    public Token(String clientID, String providerID, String scope, String tokenType, long ttl, Map providerResponse) {
        this.clientID = clientID;
        this.providerID = providerID;
        this.scope = scope;
        this.tokenType = tokenType;
        this.ttl = ttl;
        this.providerResponse = providerResponse;
    }

    //Getters
    public String getClientID() {
        return this.clientID;
    }

    public String getProviderID() {
        return this.providerID;
    }

    public String getScope() {
        return this.scope;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public long getTTL() {
        return this.ttl;
    }

    public Map getProviderResponse() {
        return this.providerResponse;
    }

    //Setters
    public void setClientID(String id) {
        this.clientID = id;
    }

    public void setProviderID(String id) {
        this.providerID = id;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTokenType(String type) {
        this.tokenType = type;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    public void setProviderResponse(Map response) {
        this.providerResponse = response;
    }
    
    public String getTokenCacheKey(){
        return providerID + "|" + clientID + "|" + scope + "|" + tokenType;
    }
    
    public String getTokenCacheKeyPrefix(){
        return providerID + "|" + clientID + "|" + scope;
    }

    @Override
    public String toString() {
        if (this.providerResponse == null) {
            return "clientID: " + this.clientID + ", providerID: " + this.providerID + ", scope: " + this.scope + ", tokenType: " + this.tokenType + ", ttl: " + this.ttl + ", providerResponse: " + null;
        } else {
            return "clientID: " + this.clientID + ", providerID: " + this.providerID + ", scope: " + this.scope + ", tokenType: " + this.tokenType + ", ttl: " + this.ttl + ", providerResponse: " + this.providerResponse.toString();

        }
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.clientID != null ? this.clientID.hashCode() : 0);
        hash = 67 * hash + (this.tokenType != null ? this.tokenType.hashCode() : 0);
        hash = 67 * hash + (this.providerID != null ? this.providerID.hashCode() : 0);
        hash = 67 * hash + (this.scope != null ? this.scope.hashCode() : 0);
        hash = 67 * hash + (int) (this.ttl ^ (this.ttl >>> 32));
        hash = 67 * hash + (this.providerResponse != null ? this.providerResponse.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (this.ttl != other.ttl) {
            return false;
        }
        if ((this.clientID == null) ? (other.clientID != null) : !this.clientID.equals(other.clientID)) {
            return false;
        }
        if ((this.tokenType == null) ? (other.tokenType != null) : !this.tokenType.equals(other.tokenType)) {
            return false;
        }
        if ((this.providerID == null) ? (other.providerID != null) : !this.providerID.equals(other.providerID)) {
            return false;
        }
        if ((this.scope == null) ? (other.scope != null) : !this.scope.equals(other.scope)) {
            return false;
        }
        if (this.providerResponse != other.providerResponse && (this.providerResponse == null || !this.providerResponse.equals(other.providerResponse))) {
            return false;
        }
        return true;
    }

}
