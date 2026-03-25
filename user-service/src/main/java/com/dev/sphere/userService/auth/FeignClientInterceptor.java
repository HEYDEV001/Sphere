package com.dev.sphere.userService.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId  = UserContextHolder.getCurrentUser();
        if(userId != null) {
            requestTemplate.header("userId", String.valueOf(userId));
        }
    }
}
