package com.blueoptima.worksample;

import com.blueoptima.worksample.model.RateLimitExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@EnableConfigurationProperties
public class WorkSampleApplication {
    public static String hostName = "localhost";
    public static int port = 6379;
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, RateLimitExecutor> redisTemplate(){
        RedisTemplate<String, RateLimitExecutor> redisTemplate= new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
    public static void main(String[] args) {
        SpringApplication.run(WorkSampleApplication.class, args);
    }

}
