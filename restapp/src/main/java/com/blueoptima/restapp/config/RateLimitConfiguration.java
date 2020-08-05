package com.blueoptima.restapp.config;

import com.blueoptima.restapp.service.RateLimitConfigurationService;
import com.blueoptima.worksample.ratelimit.RateLimitManager;
import com.blueoptima.worksample.repository.RateLimitRepositoryImpl;
import com.blueoptima.worksample.service.RateLimitExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component

public class RateLimitConfiguration {
    @Autowired
    RateLimitManager rateLimitManager;

    @Autowired
    RateLimitConfigurationService configService;

    @PostConstruct
    public void init() {
        rateMap = configService.getRateLimitMap();
        rateLimitManager.initializeRateLimitter(rateMap,timeToResetInRateLimiterMapInMs);
    }

    private Map<String, Integer> rateMap = new HashMap();

    @Value("${TIME_TO_RESET_IN_RATE_LIMITER_MAP_IN_MS}")
    private long timeToResetInRateLimiterMapInMs;

    public Map<String, Integer> getRateMap() {
        return this.rateMap;
    }

    public void setRateMap(Map<String, Integer> rateMap) {
        this.rateMap = rateMap;
    }

}
