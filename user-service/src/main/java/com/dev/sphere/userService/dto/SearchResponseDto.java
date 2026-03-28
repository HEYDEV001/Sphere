package com.dev.sphere.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDto {

    private Long id;
    private String name ;
    private Long userId;
    private String email;
    private String[] profilePicture;
    private String description;
}
