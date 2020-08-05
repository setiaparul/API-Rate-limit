package com.blueoptima.worksample.ratelimit;

import com.blueoptima.worksample.model.RateLimitExecutor;
import com.blueoptima.worksample.repository.RateLimitRepository;
import com.blueoptima.worksample.repository.RateLimitRepositoryImpl;
import com.blueoptima.worksample.service.RateLimitExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
public final class RateLimitManager {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitExecutionService.class);
    private RateLimitRepository rateLimitRepository;

    private RateLimitExecutionService rateLimitExecutionService;

    @Autowired
    public void setRateLimitRepository(RateLimitRepository rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    @Autowired
    public void setRateLimitExecutionService(RateLimitExecutionService rateLimitExecutionService) {
        this.rateLimitExecutionService = rateLimitExecutionService;
    }

/*
    public static RateLimitManager getInstance(){
        return Holder._instance;
    }
*/
    private Map<String, RateLimitExecutor> rateLimitMap = new HashMap();
    private long defaultTimeToResetInRateLimiterMapInMs = 300000;

    private long thresholdHits;


    public Map<String, RateLimitExecutor> getRateLimitMap() {
        return rateLimitMap;
    }

    public long getThresholdHits() {
        return thresholdHits;
    }

    public void setThresholdHits(long thresholdHits) {
        this.thresholdHits = thresholdHits;
    }

    /*
     * Api to initializeRateMap with rate map*/
    private void initializeRateMap(Map<String, Integer> rateMap){
        if(!CollectionUtils.isEmpty(rateMap)) {
            rateMap.forEach((key, val) -> {
                RateLimitExecutor rateLimiter = new RateLimitExecutor();
                rateLimiter.setQueue(new ConcurrentLinkedQueue<>());
                rateLimiter.setThresholdHits(val);
                rateLimiter.setTransactions(new AtomicLong(0L));
                provisionRateLimit(rateLimiter,key);
            });
        }

    }

    /*
     * Api to initializeRateLimitter with rate map*/
    public void initializeRateLimitter( Map<String, Integer> rateMap){
        initializeRateMap(rateMap);
        RateLimitExecutor.thresholdTimeToResetMap = defaultTimeToResetInRateLimiterMapInMs;
    }
    /*
     * Api to initializeRateLimitter with rate map and timeToResetInRateLimiterMapInMs*/
    public void  initializeRateLimitter(Map<String, Integer> rateMap,long timeToResetInRateLimiterMapInMs){
        initializeRateMap(rateMap);
        RateLimitExecutor.thresholdTimeToResetMap = timeToResetInRateLimiterMapInMs;
    }
    /*
     * Api to add the entry of a user+Api in rate map*/
    public void provisionRateLimit(RateLimitExecutor builder, String instance_id){
        rateLimitRepository.save(instance_id,builder);
        //rateLimitMap.put(instance_id, builder);
    }

    /*
     * Api to remove the entry of a user+Api in rate map*/
    public void deProvisionRateLimit(String instance_id){
        rateLimitRepository.delete(instance_id);
        //rateLimitMap.remove(instance_id);

    }
    /*
    * Api to evaluate the hit by a user exceed threshold limit defined in configuration file*/
    public boolean pegTraffic(String clientId, String requestUri){
        String key = clientId+requestUri;
        RateLimitExecutor rateLimitExecutor = rateLimitRepository.findById(key);
        if(rateLimitExecutor == null){
            key = requestUri;
            rateLimitExecutor = rateLimitRepository.findById(requestUri);
        }
        boolean result =   rateLimitExecutionService.evalute(rateLimitExecutor);
        if(result){
            rateLimitRepository.update(key,rateLimitExecutor);
        }
        logger.info("Status of Request of user with User Id :: {} and requestUri ::{} is true for Accepted and false for rejected. Result ::{}",clientId,requestUri,result);
        return result;
    }

}
