package com.dev.sphere.userService.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
