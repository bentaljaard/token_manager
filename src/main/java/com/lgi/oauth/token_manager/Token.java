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

    @Override
    public String toString() {
        if (this.providerResponse == null) {
            return "clientID: " + this.clientID + ", providerID: " + this.providerID + ", scope: " + this.scope + ", tokenType: " + this.tokenType + ", ttl: " + this.ttl + ", providerResponse: " + null;
        } else {
            return "clientID: " + this.clientID + ", providerID: " + this.providerID + ", scope: " + this.scope + ", tokenType: " + this.tokenType + ", ttl: " + this.ttl + ", providerResponse: " + this.providerResponse.toString();

        }
    }

}
