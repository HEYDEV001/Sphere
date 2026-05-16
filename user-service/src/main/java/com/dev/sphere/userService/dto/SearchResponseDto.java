package com.dev.sphere.userService.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SearchResponseDto implements Serializable {

    private Long id;
    private String name ;
    private Long userId;
    private String email;
    private String profilePicture;
    private String description;
}
