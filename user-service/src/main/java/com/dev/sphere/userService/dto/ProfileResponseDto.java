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
    private String[] education;
    private String[] skills;
    private String[] experience;
    private List<PostDto> posts;
}
