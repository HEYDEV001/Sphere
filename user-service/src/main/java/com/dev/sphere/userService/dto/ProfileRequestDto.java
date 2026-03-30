package com.dev.sphere.userService.dto;

import lombok.Data;

@Data
public class ProfileRequestDto {


    private String[] profilePicture;

    private String description;
    private String[] education;
    private String[] skills;
    private String[] experience;
}
