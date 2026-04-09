package com.dev.sphere.userService.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetProfileResponseDto {

    private Long id;
    private String name ;
    private Long userId;
    private String email;
    private String profilePicture;
    private String description;
    private List<String> education;
    private List<String> skills;
    private List<String> experience;
    private List<PostDto> posts;
}
