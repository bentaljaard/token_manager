package com.lgi.oauth.token_manager;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager.Listener;
import com.google.common.util.concurrent.ServiceManager;
import static com.lgi.oauth.token_manager.GrantType.REQUIRED_PARAMETERS;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.auth.AuthenticationException;

public class TokenManager {

    private TokenCache cache;
    private static final Logger logger = Logger.getLogger(TokenManager.class.getName());
    private ConcurrentMap providerSemaphores;
    private ConcurrentMap degradedProviders;
    ServiceManager manager;
    private int connectTimeoutMS;
    private int socketTimeoutMS;
    private long clientWaitTimeMS;
    private int healthCheckIntervalMS;

    public TokenManager() {
        super();
        this.setupTokenCache();
        providerSemaphores = new ConcurrentHashMap<String, Semaphore>();
        degradedProviders = new ConcurrentHashMap<String, List>();
        connectTimeoutMS = 15000;
        socketTimeoutMS = 60000;
        clientWaitTimeMS = 120000;
        healthCheckIntervalMS = 60000;

    }

    public void startHealthCheck(int intervalMS) {
        healthCheckIntervalMS = intervalMS;
        startHealthCheck();
    }

    public void startHealthCheck() {
        Service healthCheckService = new ProviderHealthChecker(degradedProviders, healthCheckIntervalMS);
        Set<Service> services = new HashSet();
        services.add(healthCheckService);
        manager = new ServiceManager(services);
        manager.addListener(new Listener() {
            public void stopped() {
            }

            public void healthy() {
                // Services have been initialized and are healthy, start accepting requests...
            }

            public void failure(Service service) {
                // Something failed, at this point we could log it, notify a load balancer, or take
                // some other action.  For now we will just exit.
                System.exit(1);
            }
        },
                MoreExecutors.directExecutor());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // Give the services 5 seconds to stop to ensure that we are responsive to shutdown
                // requests.
                try {
                    manager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
                } catch (TimeoutException timeout) {
                    // stopping timed out
                }
            }
        });
        manager.startAsync();  // start all the services asynchronously
    }

    public void stopHealthCheck() {
        try {
            manager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
        } catch (TimeoutException timeout) {
            // stopping timed out
        }
    }

    public ImmutableMultimap<Service.State, Service> getHealthCheckStatus() {
        return manager.servicesByState();
    }

    public void setConnectTimeout(int ms) {
        connectTimeoutMS = ms;
    }

    public void setSocketTimeout(int ms) {
        socketTimeoutMS = ms;
    }

    public void setClientWaitTime(long ms) {
        clientWaitTimeMS = ms;
    }

    public void setHealthCheckInterval(int ms) {
        healthCheckIntervalMS = ms;
    }

    private Token loadCache(OAuthClient client, Provider provider, Map params, String k) throws AuthenticationException, IOException, UnknownHostException, SocketException {
        Token token;
        Token refreshToken = null;

        //Check if token has not been added to the cache by another thread
        if (this.getTokenCache().containsToken(k)) {
            return this.getTokenCache().retrieveToken(k);
        }

        //Does a refresh token exist in the cache
        String refreshTokenKey = k.substring(0, k.lastIndexOf("|")) + "|refresh_token";
        boolean hasRefreshToken = this.getTokenCache().containsToken(refreshTokenKey);

        if (hasRefreshToken) {
            logger.log(Level.FINE, "Token {0} found in cache", refreshTokenKey);

            //get refresh token from cache
            refreshToken = this.getTokenCache().retrieveToken(refreshTokenKey);

            token = client.refreshToken(provider, (String) refreshToken.getProviderResponse().get("refresh_token"));
            if (token == null) {
                throw new NullPointerException("Token returned is null");
            }
            logger.log(Level.INFO, "Got new access token from provider using refresh token");

        } else {
            //Authenticate to get new access token

            token = (Token) client.getToken(provider);
            if (token == null) {
                throw new NullPointerException("Token returned is null");
            }
            logger.log(Level.INFO, "Got new access token from provider {0}", provider.getID());

        }
//        System.out.println(token.toString());
        //Does response contain a refresh token, cache refresh or update what we already have
        if (token.getProviderResponse()
                .containsKey("refresh_token")) {
            logger.log(Level.FINE, "Response contains a refresh token");

            if (hasRefreshToken) {
                // did the token change, update or replace
                if (!(refreshToken.getProviderResponse().get("refresh_token").equals(token.getProviderResponse().get("refresh_token")))) {
                    //replace existing refresh token
                    Token newToken = new Token();
                    newToken.setClientID(token.getClientID());
                    newToken.setProviderID(token.getProviderID());
                    newToken.setScope(token.getScope());
                    newToken.setTTL(Integer.parseInt((String) params.get("refresh_token_ttl")));
                    newToken.setTokenType("refresh_token");
                    newToken.setProviderResponse(token.getProviderResponse());
                    this.getTokenCache().replaceTokenWithTTL(refreshToken, newToken, Integer.parseInt((String) params.get("refresh_token_ttl")));
                    logger.log(Level.INFO, "Updated refresh token with a new value");
                } else {
                    this.getTokenCache().replaceToken(refreshToken, token);
                    logger.log(Level.INFO, "Refresh token still valid, updating access token");
                }
            } else {
                logger.log(Level.FINE, "No refresh token was found in cache, adding it");
                refreshToken = new Token();
                refreshToken.setClientID(token.getClientID());
                refreshToken.setProviderID(token.getProviderID());
                refreshToken.setScope(token.getScope());
                refreshToken.setTTL(Integer.parseInt((String) params.get("refresh_token_ttl")));
                refreshToken.setTokenType("refresh_token");
                refreshToken.setProviderResponse(token.getProviderResponse());
                this.getTokenCache().cacheToken(refreshToken);
                logger.log(Level.INFO, "Added refresh token to the token cache");
            }

        }
        this.getTokenCache().cacheToken(token);
        logger.log(Level.INFO, "Added access token to the token cache");
        return token;
    }

    private Token getToken(OAuthClient client) {
        Token token = null;
        Semaphore sem = null;
        boolean providerLock = false;
        try {
            //Check if we have valid parameters to interact with the cache
            if (!validParameters(client.getParams())) {
                return errorToken("Not all required parameters provided to get a token");
            }
            Provider provider = new Provider((String) client.getParams().get("provider_url"), connectTimeoutMS, socketTimeoutMS);
            token = this.getTokenCache().retrieveToken(getTokenCacheKeyPrefix(client.getParams(), provider) + "|access_token");

            //if token cannot be found in cache, then get it from the provider and populate cache
            if (token == null) {

                if (manager != null && degradedProviders.containsKey(provider.getID()) && manager.isHealthy()) {
                    logger.log(Level.FINEST, "Provider {0} in a degraded state, returning status", provider.getID());
                    return errorToken("Oauth provider is in a degraded state, retry later");
                } else {
                    if (!providerSemaphores.containsKey(provider.getID())) {
                        //Add semaphore in map so that we can lock on provider
                        providerSemaphores.put(provider.getID(), new Semaphore(1));
                    }

                    sem = (Semaphore) providerSemaphores.get(provider.getID());
                    //Try to get the provider lock
                    providerLock = sem.tryAcquire(clientWaitTimeMS, TimeUnit.MILLISECONDS);
                    if (providerLock) {
                        try {
                            if (manager != null && degradedProviders.containsKey(provider.getID()) && manager.isHealthy()) {
                                sem.release();
                                logger.log(Level.FINEST, "Provider {0} in a degraded state, returning status", provider.getID());
                                return errorToken("Oauth provider is in a degraded state, retry later");
                            }
                            token = this.loadCache(client, provider, client.getParams(), getTokenCacheKeyPrefix(client.getParams(), provider) + "|access_token");
                            //Release provider lock
                            sem.release();
                        } catch (SocketTimeoutException e) {
                            //A socket exception occured, flag provider as degraded
                            if (manager != null && manager.isHealthy()) {
                                List healthcheck = new ArrayList();
                                healthcheck.add(provider);
                                healthcheck.add(client);
                                degradedProviders.put(provider.getID(), healthcheck);
                            }

                            sem.release();
                            logger.log(Level.SEVERE, "Timeout occured requesting token from provider {0}. Added to list of degraded providers.", provider.getID());
                            return errorToken("Timeout occured requesting token from provider");
                        }

                    } else {
                        return errorToken("Client wait time has been exceeded, timing out");
                    }
                }
            }
            return token;
        } catch (Exception e) {
            return errorToken(e.getMessage());
        }

    }

    private Token errorToken(String message) {
        Token errorToken;
        logger.log(Level.SEVERE, message);
        Map error = new HashMap();
        error.put("error", message);
        errorToken = new Token();
        errorToken.setTokenType("error_token");
        errorToken.setProviderResponse(error);
        return errorToken;
    }

    public TokenCache getTokenCache() {
        return this.cache;
    }

    private Boolean validParameters(Map params) {
        return Util.validParameters(params, REQUIRED_PARAMETERS);

    }

    private String getTokenCacheKeyPrefix(Map params, Provider provider) {
        return provider.getID() + "|" + (String) params.get("client_id") + "|" + (String) params.get("scope");
    }

    private void setupTokenCache() {
        cache = new TokenCacheImpl();
        logger.log(Level.INFO, "Setup token cache");
    }

    public void clearTokenCache() {
        this.cache.clearCache();
        logger.log(Level.INFO, "Cleared token cache");
    }

    public Token getBearerToken(OAuthClient client) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Token token = getToken(client);
        stopwatch.stop();
        logger.log(Level.FINE, "Time to get token: {0}", stopwatch);

        return token;
    }

}
