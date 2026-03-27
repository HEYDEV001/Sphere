package com.dev.sphere.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto {

    private String name;
    private String[] profilePicture;
    private String description;


}
