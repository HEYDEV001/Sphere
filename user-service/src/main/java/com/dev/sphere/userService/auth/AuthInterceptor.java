package com.dev.sphere.userService.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        System.out.println("🔥 AUTH HIT: " + path);

        // 🎯 Apply auth ONLY to required APIs
        if (path.contains("/api/v1/user/profile/create") ||
                path.contains("/api/v1/user/profile/getProfile")) {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(401);
                response.getWriter().write("Unauthorized");
                return false;
            }
        }

        return true;
    }
}
