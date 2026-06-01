package com.dev.sphere.notification_service.consumer;

import com.dev.sphere.notification_service.service.EmailService;
import com.dev.sphere.userService.event.PasswordResetEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetConsumer {
    private final EmailService emailService;

    @Value("${password.reset.base-url}")
    private String baseUrl;

    @KafkaListener(topics = "password-reset-topic")
    public void handlePasswordReset(PasswordResetEvent event) throws IOException {
        log.info("PasswordResetEvent received for email: {}", event.getEmail());

        String resetLink = baseUrl + "/reset-password?token=" + event.getToken();

        emailService.sendPasswordResetEmail(
                event.getEmail(),
                event.getName(),
                resetLink
        );

        log.info("Password reset email sent to: {}", event.getEmail());
    }
}
