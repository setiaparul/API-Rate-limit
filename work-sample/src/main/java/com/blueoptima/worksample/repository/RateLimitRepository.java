package com.blueoptima.worksample.repository;


import com.blueoptima.worksample.model.RateLimitExecutor;

import java.util.List;
import java.util.Map;

public interface RateLimitRepository {
    void save(String clientId,RateLimitExecutor rateLimitObj);
    Map<String,RateLimitExecutor> findAll();
    void update(String clientId,RateLimitExecutor rateLimitObj);
    void delete(String clientId);
    RateLimitExecutor findById(String clientId);
}
