//package com.dev.sphere.userService.config;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.Set;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class CircuitBreakerCacheEvictConfig {
//
//    private final CircuitBreakerRegistry circuitBreakerRegistry;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    @PostConstruct
//    public void RegisterCircuitBreakerEvent() {
//        CircuitBreaker circuitBreaker =circuitBreakerRegistry.circuitBreaker("connection-service");
//        circuitBreaker.getEventPublisher().onStateTransition(event -> {
//
//            if(event.getStateTransition() == CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED){
//                log.info("Circuit is now closed - Evicting stale cache of connection-service");
//                Set<String> keys = redisTemplate.keys("sphere:connection:first:*");
//                if(keys != null && !keys.isEmpty()){
//                    redisTemplate.delete(keys);
//                    log.info("Evicted all the stale caches of connection-service");
//                }
//            }
//        });
//    }
//
//}
