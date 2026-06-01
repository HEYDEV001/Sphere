package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.ForgotPasswordRequestDto;
import com.dev.sphere.userService.dto.ResetPasswordRequestDto;
import com.dev.sphere.userService.entity.User;
import com.dev.sphere.userService.event.PasswordResetEvent;
import com.dev.sphere.userService.exception.BadRequestException;
import com.dev.sphere.userService.exception.ResourceNotFoundException;
import com.dev.sphere.userService.repository.UserRepository;
import com.dev.sphere.userService.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    @Value("${password.reset.token.expiry}")
    private long tokenExpiry;

    @Value("${password.reset.base.url}")
    private String baseUrl;

    private static final String RESET_TOKEN_PREFIX = "sphere:password:reset";
    private static final String TOPIC = "password-reset-topic";
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<Long, PasswordResetEvent> kafkaTemplate;
    @Override
    public void forgotPassword(ForgotPasswordRequestDto requestDto) {
        log.info("Forgot password request for the user with email: {}", requestDto.getEmail());

        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            String cacheKey = RESET_TOKEN_PREFIX + token;

            redisTemplate.opsForValue()
                    .set(cacheKey, user.get().getEmail(), Duration.ofSeconds(tokenExpiry));
            log.info("Reset token stored in Redis for userId: {}", user.get().getId());

            PasswordResetEvent passwordResetEvent = PasswordResetEvent.builder()
                    .token(token)
                    .email(user.get().getEmail())
                    .name(user.get().getName())
                    .build();


            kafkaTemplate.send(TOPIC, user.get().getId(), passwordResetEvent);
        }

    }

    @Override
    public void resetPassword(ResetPasswordRequestDto requestDto) {
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String cacheKey = RESET_TOKEN_PREFIX + requestDto.getToken();

        String email = redisTemplate.opsForValue().get(cacheKey);
        if (email == null) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(PasswordUtil.hashPassword(requestDto.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(cacheKey);
        log.info("Password reset successfully for userId: {}", user.getId());
    }
}
