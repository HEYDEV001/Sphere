package com.dev.sphere.userService.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class GetProfileResponseDto implements Serializable {

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
