package com.dev.sphere.userService.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileRequestDto {


    private String profilePicture;
    private String description;
    private List<String> education;
    private List<String> skills;
    private List<String> experience;
}
