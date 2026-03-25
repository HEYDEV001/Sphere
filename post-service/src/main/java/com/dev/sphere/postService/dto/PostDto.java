package com.dev.sphere.postService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private Long likesCount;
}
