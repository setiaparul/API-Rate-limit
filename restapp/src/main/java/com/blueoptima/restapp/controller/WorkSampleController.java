package com.blueoptima.restapp.controller;

import com.blueoptima.restapp.config.RateLimitConfiguration;
import com.blueoptima.worksample.ratelimit.RateLimitManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/api")
public class WorkSampleController {

    @Autowired
    RateLimitConfiguration rateLimitConfiguration;
    @Autowired
    RateLimitManager rateLimitManager;

    @RequestMapping("/v1/developers")
    public ResponseEntity getDevelopers(HttpServletRequest request) {
        String clientId = request.getHeader("ClientId") != null ?request.getHeader("ClientId") :"";
        if(rateLimitManager.pegTraffic(clientId,request.getRequestURI())){
            List<String> name= new ArrayList();
            name.add("John");
            name.add("Ravi");
            return new ResponseEntity(name, HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @RequestMapping("/v1/organizations")
    public ResponseEntity getOrganizations(HttpServletRequest request) {
        String clientId = request.getHeader("ClientId") != null ?request.getHeader("ClientId") :"";
        if(rateLimitManager.pegTraffic(clientId,request.getRequestURI())){
            List<String> name= new ArrayList();
            name.add("Blue Optima");
            name.add("Jp Morgan");
            return new ResponseEntity(name, HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}

