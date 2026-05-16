package com.dev.sphere.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String JWT_CACHE_PREFIX = "sphere:user:jwt:";
    private static final Duration JWT_TTL = Duration.ofMinutes(10);

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getIdFromTheToken(String token) {
        String cacheKey = JWT_CACHE_PREFIX + token;

        String cachedUserId = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUserId != null) {
            log.info("JWT Cache HIT — userId: {}", cachedUserId);
            return cachedUserId;
        }

        log.info("JWT Cache MISS for userId: {}", token);
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.getSubject();
        redisTemplate.opsForValue().set(cacheKey, userId, JWT_TTL);

        return userId;
    }

}
