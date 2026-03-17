package com.dev.sphere.postService.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
}
