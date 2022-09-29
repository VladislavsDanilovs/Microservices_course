package com.vladdanilov.microservice.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/sample-api")
//    @Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")  // for small server interrupts (from 1s - 60s approx)
    @CircuitBreaker(name="default", fallbackMethod = "hardcodedResponse") // if server goes down for a long time
    @RateLimiter(name="default") // rate limiting means that for instance we want to allow in 10s only 10000 calls to sample api
    @Bulkhead(name="default") // how many concurrent calls are allowed
    public String sampleApi() {

        logger.info("Sample API call received");

        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/notexist", String.class);

        return forEntity.getBody();
    }

    public String hardcodedResponse(Exception ex){
        return "fallback-random-response";
    }

}
