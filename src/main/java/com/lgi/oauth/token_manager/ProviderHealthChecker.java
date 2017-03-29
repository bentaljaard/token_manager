/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lgi.oauth.token_manager;

import com.google.common.util.concurrent.AbstractScheduledService;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author btaljaard
 */
public class ProviderHealthChecker extends AbstractScheduledService {

    private ConcurrentMap degradedProviders;
    private static final Logger logger = Logger.getLogger(ProviderHealthChecker.class.getName());
    private int checkInterval;

    public ProviderHealthChecker(ConcurrentMap providers, int intervalMS) {
        degradedProviders = providers;
        checkInterval = intervalMS;
    }
    
    @Override
    protected void shutDown(){
        degradedProviders.clear();
        logger.log(Level.INFO, "Signal received to stop Provider health check");
    }

    @Override
    protected void runOneIteration() throws Exception {
        //Iterate over providers and see if we can do a successfull request
        //Remove from map if request was a success
        if (!degradedProviders.isEmpty()) {
            Iterator it = degradedProviders.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                logger.log(Level.INFO, "Running health check for provider {0}", pair.getKey());
                List healthcheck = (List) degradedProviders.get(pair.getKey());
                Provider testProvider = (Provider) healthcheck.get(0);
                OAuthClient testClient = (OAuthClient) healthcheck.get(1);
                try {
                    Token response = testClient.getToken(testProvider);
                    if (response != null) {
                        degradedProviders.remove(pair.getKey());
                        logger.log(Level.INFO, "Provider {0} has recovered from degraded state", pair.getKey());
                    }
                } catch (SocketTimeoutException e) {
                    //Provider still has issues
                    logger.log(Level.INFO, "Provider {0} is still in a degraded state", pair.getKey());
                }

            }
        }

    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, checkInterval, TimeUnit.MILLISECONDS);
    }

}
