package com.dev.sphere.userService.auth;

import com.dev.sphere.userService.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        if (path.contains("/profile/create") ||
                path.contains("/profile/getProfile")) {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(401);
                response.getWriter().write("Unauthorized");
                return false;
            }

            final String token = authHeader.split("Bearer ")[1];

            try {
                Long userId = jwtService.getIdFromTheToken(token);
                if(userId != null) {
                    return true;
                }
                return false;
            } catch (JwtException e) {
                log.error("Jwt Exception {}", e.getLocalizedMessage());
                throw new JwtException("Jwt Exception : " + e.getLocalizedMessage());
            }
        }

        return true;
    }
}
