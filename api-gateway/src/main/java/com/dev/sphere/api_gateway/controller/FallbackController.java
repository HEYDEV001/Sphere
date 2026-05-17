package com.dev.sphere.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @RequestMapping("/post-service")
    public ResponseEntity<Map<String, String>> postServiceFallback() {
        log.warn("Post service circuit breaker opened — returning fallback");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                    "status", "SERVICE_UNAVAILABLE",
                    "message", "Post service is currently unavailable. Please try again later.",
                    "service", "post-service"
                ));
    }

    @RequestMapping("/connection-service")
    public ResponseEntity<Map<String, String>> connectionServiceFallback() {
        log.warn("Connection service circuit breaker opened — returning fallback");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                    "status", "SERVICE_UNAVAILABLE",
                    "message", "Connection service is currently unavailable. Please try again later.",
                    "service", "connection-service"
                ));
    }
}