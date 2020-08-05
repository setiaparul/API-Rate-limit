package com.blueoptima.worksample;

import com.blueoptima.worksample.ratelimit.RateLimitManager;
import com.blueoptima.worksample.repository.RateLimitRepository;
import com.blueoptima.worksample.repository.RateLimitRepositoryImpl;
import com.blueoptima.worksample.service.RateLimitExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fppt.jedismock.RedisServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RateLimitManagerTest {

    private Map<String, Integer> rateMap = new HashMap();
    private long timeToResetInRateLimiterMapInMs;
    private long threshholdTime;
    RateLimitManager rateLimitManager;
    private static RedisServer server = null;
    ObjectMapper mapper = new ObjectMapper();
    @Before
    public void init() throws IOException {
        server = RedisServer.newRedisServer();
        server.start();
        //server

        rateMap.put("/api/v1/developers",150);
        rateMap.put("/api/v1/organizations",150);
        rateMap.put("user1/api/v1/developers",3);
        rateMap.put("user2/api/v1/developers",50);
        rateMap.put("user1/api/v1/organizations",250);
        rateMap.put("user2/api/v1/organizations",500);
        timeToResetInRateLimiterMapInMs = 300000;
        threshholdTime = 100;
        rateLimitManager = new RateLimitManager();
        RateLimitExecutionService service = new RateLimitExecutionService();
        RateLimitRepositoryImpl repository = new RateLimitRepositoryImpl();
        repository.setMapper(mapper);
        rateLimitManager.setRateLimitRepository(repository);
        rateLimitManager.setRateLimitExecutionService(service);
        rateLimitManager.initializeRateLimitter(rateMap,timeToResetInRateLimiterMapInMs);
    }

    @Test
    public void defaultRateLimitPassTestWithNoOfHitsLessThanThreshold(){
        boolean isPass = false;
        for(int i=1;i<25;i++){
           isPass =  rateLimitManager.pegTraffic("","/api/v1/developers");
        }
        Assert.assertEquals(true,isPass);
    }

    @Test
    public void defaultRateLimitFailTestWithNoOfHitsMoreThanThreshold(){
        boolean isPass = false;
        for(int i=1;i<160;i++){
            isPass =  rateLimitManager.pegTraffic("","/api/v1/developers");
        }
        Assert.assertEquals(false,isPass);
    }

    @Test
    public void userRateLimitPassTestWithNoOfHitsLessThanThreshold(){
        boolean isPass = false;
        for(int i=1;i<25;i++){
            isPass =  rateLimitManager.pegTraffic("","user1/api/v1/developers");
        }
        Assert.assertEquals(true,isPass);
    }

    @Test
    public void userRateLimitFailTestWithNoOfHitsMoreThanThreshold(){
        boolean isPass = false;
        for(int i=1;i<30;i++){
            System.out.println("Count of iteration"+ i);
            isPass =  rateLimitManager.pegTraffic("","user1/api/v1/developers");
        }
        Assert.assertEquals(false,isPass);
    }

    @Test
    public void defaultTestWithNoOfHitsMoreThanThresholdButTimeMoreThanRefreshThreshold() throws InterruptedException {
        rateLimitManager.initializeRateLimitter(rateMap,30000);
        boolean isPass = false;
        for(int i=1;i<30;i++){
            Thread.sleep(1500);
            System.out.println("Count of iteration"+ i);
            isPass =  rateLimitManager.pegTraffic("","user1/api/v1/developers");
        }
        Assert.assertEquals(true,isPass);

    }

}
