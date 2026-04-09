package com.dev.sphere.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto {

    private String name;
    private String profilePicture;
    private String description;
    private List<String> education;
    private List<String> skills;
    private List<String> experience;


}
