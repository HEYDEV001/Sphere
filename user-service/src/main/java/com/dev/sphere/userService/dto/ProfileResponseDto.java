package com.dev.sphere.userService.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileResponseDto {

    private Long id;
    private String name ;
    private Long userId;
    private String email;
    private String[] profilePicture;
    private String description;
    private List<PostDto> posts;
}
