package com.dev.sphere.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedProfileResponseDto {

    private Long id;
    private String name ;
    private Long userId;
    private String email;
    private String[] profilePicture;
    private String description;
}
