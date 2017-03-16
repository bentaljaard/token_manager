# Token Manager

The token manager library is used to provide a cache mechanism for OAuth2.0 tokens used in your application. This is a generic implementation that can be used for the supported grants. Client applications will instanciate a new TokenManager instance and use the getBearerToken method to get valid access tokens. If the grant type supports refresh tokens, the refresh token will be used to get a new access token. This happens automatically if the OAuth provider returns a refresh token in the original response. Detailed usage is documented below in the sections that cover the specific grants.

## Supported Grants

#### Client Credentials

http://tools.ietf.org/html/rfc6749#section-4.4

The client can request an access token using only its client credentials (or other supported means of authentication) when the client is requesting access to the protected resources under its control, or those of another resource owner that have been previously arranged with the authorization server (the method of which is beyond the scope of this specification).

The client credentials grant type MUST only be used by confidential clients.

```
+---------+                                  +---------------+
|         |                                  |               |
|         |>--(A)- Client Authentication --->| Authorization |
| Client  |                                  |     Server    |
|         |<--(B)---- Access Token ---------<|               |
|         |                                  |               |
+---------+                                  +---------------+
```

The client authenticates with the authorization server and requests an access token from the token endpoint. Client authentication is possible with either Basic Authentication (username and password) or by passing client_id and client_secret in the body.

The authorization server authenticates the client, and if valid, issues an access token.

```json
{
  "access_token": "00ccd40e-72ca-4e79-a4b6-67c95e2e3f1c",
  "expires_in": 3600,
  "token_type": "Bearer",
  "scope": "read_write",
  "refresh_token": "6fd8d272-375a-4d8a-8d0f-43367dc8b791"
}
```

##### Usage Example:


###### In your application constructor:


```

tm = new TokenManager();

```

###### In your method requiring the access token:

```

Map params = new HashMap();
params.put("provider_url","https://some_auth_provider_url.com/token");   //required
params.put("provider_id", "some_provider_identifier");  //required
params.put("client_id", "your_client_id");  //required
params.put("client_secret", "your_client_secret"); //when using id and secret to authenticate
params.put("basic_username", "your_basic_auth_user");    //when using basic authentication
params.put("basic_password", "your_basic_auth_password");   //when using basic authentication
params.put("scope", "your_requested_application_scope");   //optional if scopes are required
params.put("access_token_ttl", "your_access_token_ttl_in_seconds"); //optional, if provider does not return an expiry time for the access token in the response


Token token = (Token)tm.getBearerToken(params);

if(token.getTokenType() == "error_token"){
  String errorMessage = token.getProviderResponse().get("error");
  // Do something with the error
} else {
  String accessToken = token.getProviderResponse().get("access_token");
}


```



#### Resource Owner Password Credentials

http://tools.ietf.org/html/rfc6749#section-4.3

The resource owner password credentials grant type is suitable in cases where the resource owner has a trust relationship with the client, such as the device operating system or a highly privileged application. The authorization server should take special care when enabling this grant type and only allow it when other flows are not viable.

This grant type is suitable for clients capable of obtaining the resource owner's credentials (username and password, typically using an interactive form). It is also used to migrate existing clients using direct authentication schemes such as HTTP Basic or Digest authentication to OAuth by converting the stored credentials to an access token.

```
+----------+
| Resource |
|  Owner   |
|          |
+----------+
     v
     |    Resource Owner
     (A) Password Credentials
     |
     v
+---------+                                  +---------------+
|         |>--(B)---- Resource Owner ------->|               |
|         |         Password Credentials     | Authorization |
| Client  |                                  |     Server    |
|         |<--(C)---- Access Token ---------<|               |
|         |    (w/ Optional Refresh Token)   |               |
+---------+                                  +---------------+

```

The resource owner provides the client with its username and password.

The client requests an access token from the authorization server's token endpoint by including the credentials received from the resource owner. When making the request, the client authenticates with the authorization server.


The authorization server authenticates the client and validates the resource owner credentials, and if valid, issues an access token.

```json
{
  "user_id": 1,
  "access_token": "00ccd40e-72ca-4e79-a4b6-67c95e2e3f1c",
  "expires_in": 3600,
  "token_type": "Bearer",
  "scope": "read_write",
  "refresh_token": "6fd8d272-375a-4d8a-8d0f-43367dc8b791"
}
```

##### Usage Example:


###### In your application constructor:


```

tm = new TokenManager();

```

###### In your method requiring the access token:

```

Map params = new HashMap();
params.put("provider_url","https://some_auth_provider_url.com/token");   //required
params.put("provider_id", "some_provider_identifier");  //required
params.put("client_id", "your_client_id");  //required
params.put("client_secret", "your_client_secret"); //when using id and secret to authenticate
params.put("basic_username", "your_basic_auth_user");    //when using basic authentication
params.put("basic_password", "your_basic_auth_password");   //when using basic authentication
params.put("username","your_resource_username");  //required
params.put("password", "your_resource_password");  //required
params.put("scope", "your_requested_application_scope");   //optional if scopes are required
params.put("refresh_token_ttl", "your_refresh_token_ttl_in_seconds");   //required, specify a duration to keep the refresh token in the cache
params.put("access_token_ttl", "your_access_token_ttl_in_seconds"); //optional, if provider does not return an expiry time for the access token in the response

Token token = (Token)tm.getBearerToken(params);

if(token.getTokenType() == "error_token"){
  String errorMessage = token.getProviderResponse().get("error");
  // Do something with the error
} else {
  String accessToken = token.getProviderResponse().get("access_token");
}


```



### Refreshing An Access Token

http://tools.ietf.org/html/rfc6749#section-6

If the authorization server issued a refresh token to the client, the client can make a refresh request to the token endpoint in order to refresh the access token.

The authorization server MUST:

* require client authentication for confidential clients or for any client that was issued client credentials (or with other authentication requirements),

* authenticate the client if client authentication is included and ensure that the refresh token was issued to the authenticated client, and

* validate the refresh token.

If valid and authorized, the authorization server issues an access token.

```json
{
  "user_id": 1,
  "access_token": "1f962bd5-7890-435d-b619-584b6aa32e6c",
  "expires_in": 3600,
  "token_type": "Bearer",
  "scope": "read_write",
  "refresh_token": "3a6b45b8-9d29-4cba-8a1b-0093e8a2b933"
}
```

The authorization server MAY issue a new refresh token, in which case the client MUST discard the old refresh token and replace it with the new refresh token.  The authorization server MAY revoke the old refresh token after issuing a new refresh token to the client.  If a new refresh token is issued, the refresh token scope MUST be identical to that of the refresh token included by the client in the request.