package com.blueoptima.restapp.service;

import com.blueoptima.restapp.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RateLimitConfigurationService {

    @Autowired
    FileUtils fileUtils;

    public Map<String,Integer> getRateLimitMap(){
        return fileUtils.getUserApiMapping();
    }
}
