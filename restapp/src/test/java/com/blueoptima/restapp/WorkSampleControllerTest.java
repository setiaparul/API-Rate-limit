package com.blueoptima.restapp;

import com.blueoptima.restapp.controller.WorkSampleController;
import com.blueoptima.worksample.ratelimit.RateLimitManager;
import com.blueoptima.worksample.repository.RateLimitRepository;
import com.blueoptima.worksample.repository.RateLimitRepositoryImpl;
import com.blueoptima.worksample.service.RateLimitExecutionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class WorkSampleControllerTest {

    private Map<String, Integer> rateMap = new HashMap();
    private long timeToResetInRateLimiterMapInMs;
    private long threshholdTime;

    WorkSampleController controller;
    RateLimitManager rateLimitManager;
    @Before
    public void init() {
        rateMap.put("/api/v1/developers",150);
        rateMap.put("/api/v1/organizations",150);
        rateMap.put("user1/api/v1/developers",25);
        rateMap.put("user2/api/v1/developers",50);
        rateMap.put("user1/api/v1/organizations",250);
        rateMap.put("user2/api/v1/organizations",500);
        timeToResetInRateLimiterMapInMs = 300000;
        threshholdTime = 30000;
        controller = new WorkSampleController();
        rateLimitManager = new RateLimitManager();
        RateLimitExecutionService service = new RateLimitExecutionService();
        RateLimitRepository repository = new RateLimitRepositoryImpl();
        rateLimitManager.setRateLimitRepository(repository);
        rateLimitManager.setRateLimitExecutionService(service);

        rateLimitManager.initializeRateLimitter(rateMap,threshholdTime);
    }
    @Test
    public void defaultRateLimitPassTestWithNoOfHitsLessThanThreshold(){
        ResponseEntity response = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/developers");
        for(int i=1;i<25;i++){
            response = controller.getDevelopers(request);
        }
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void defaultRateLimitFailTestWithNoOfHitsMoreThanThreshold(){
        ResponseEntity response = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/developers");
        for(int i=1;i<160;i++){
            response = controller.getDevelopers(request);
        }
        Assert.assertEquals(response.getStatusCode(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    public void userRateLimitPassTestWithNoOfHitsLessThanThreshold(){
        ResponseEntity response = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/developers");
        request.addHeader("ClientId","user1");
        for(int i=1;i<25;i++){
            response = controller.getDevelopers(request);
        }
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void userRateLimitFailTestWithNoOfHitsMoreThanThreshold(){
        ResponseEntity response = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/developers");
        request.addHeader("ClientId","user1");
        for(int i=1;i<50;i++){
            response = controller.getDevelopers(request);
        }
        Assert.assertEquals(response.getStatusCode(), HttpStatus.TOO_MANY_REQUESTS);
    }

}
