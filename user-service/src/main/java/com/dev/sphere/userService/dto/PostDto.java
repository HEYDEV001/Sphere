package com.dev.sphere.userService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private Long userId;
    private String[] images;
    private String content;
    private Long likesCount;
    private LocalDateTime createdAt;

}
