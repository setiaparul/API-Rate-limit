package com.blueoptima.worksample.repository;

import com.blueoptima.worksample.model.RateLimitExecutor;
import com.blueoptima.worksample.ratelimit.RateLimitManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static javafx.scene.input.KeyCode.G;

@Repository
public class RateLimitRepositoryImpl implements RateLimitRepository{

    @Autowired
    private RedisTemplate redisTemplate;

    private HashOperations hashOperations;

    @Autowired
    private ObjectMapper mapper;

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {

        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String clientId,RateLimitExecutor rateLimitObj) {


        try {
            Jedis jedis = new Jedis();
            String jsonString = mapper.writeValueAsString(rateLimitObj);
            jedis.del(clientId);
            jedis.set(clientId,jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public Map<String,RateLimitExecutor> findAll() {
        return hashOperations.entries("RATELIMIT");
    }

    @Override
    public void update(String clientId,RateLimitExecutor rateLimitObj) {
        save(clientId,rateLimitObj);
    }

    @Override
    public void delete(String clientId) {
        hashOperations.delete("RATELIMIT",clientId);
    }

    @Override
    public RateLimitExecutor findById(String clientId) {

        try {
            Jedis jedis = new Jedis();
            System.out.println((String) jedis.get(clientId));
            //String jsonObj = (String) hashOperations.get("RATELIMIT", clientId);
            String jsonObj = (String) jedis.get(clientId);
            return mapper.readValue(jsonObj, RateLimitExecutor.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;

        }
    }
}
