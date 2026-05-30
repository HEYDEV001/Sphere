package com.dev.sphere.userService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic passwordResetTopic(){
        return new NewTopic("password-reset-topic", 3, (short) 1);
    }
}
