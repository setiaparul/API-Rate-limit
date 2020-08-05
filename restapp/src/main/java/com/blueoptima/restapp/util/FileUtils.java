package com.blueoptima.restapp.util;

import com.blueoptima.restapp.model.UserApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileUtils {
    private static final Logger LOGGER= LoggerFactory.getLogger(FileUtils.class);

    @Autowired
    ObjectMapper mapper;

    public static String CONFIG_FILE = "config.json";
    public Map<String,Integer> getUserApiMapping(){
        Map<String,Integer> configMap = new HashMap<>();
        try {
            UserApiConfig[] configs =  mapper.readValue(new File(this.getClass().getClassLoader().getResource("").getPath()+"/"+CONFIG_FILE)
                , UserApiConfig[].class);
            for(UserApiConfig config: configs){
                if(config.getUserId()!=null){
                    configMap.put(config.getUserId()+config.getApi() , config.getLimit());
                }else{
                    configMap.put(config.getApi() , config.getLimit());
                }

            };

        } catch (IOException e) {
            LOGGER.error("Exception because of IO Error while reading User Details from config.json ",e);
            LOGGER.debug("Need to restart the application, for loading rateLimit Map");
        }
        return configMap;
    }

}
