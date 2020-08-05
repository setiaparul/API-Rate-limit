package com.blueoptima.restapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages ={"com.blueoptima.worksample.*","com.blueoptima.restapp.*"})
public class RestappApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestappApplication.class, args);
    }


}
