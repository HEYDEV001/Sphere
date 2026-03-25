package com.dev.sphere.userService.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserInterceptor userInterceptor;
    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 🔒 Secure only these APIs
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**");

        // 🌍 Apply user context everywhere
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**");
    }

}
