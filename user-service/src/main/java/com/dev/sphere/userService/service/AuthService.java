package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.LoginDto;
import com.dev.sphere.userService.dto.LoginResponseDto;
import com.dev.sphere.userService.dto.SignUpDto;
import com.dev.sphere.userService.dto.UserDto;

public interface AuthService {
    UserDto signUp(SignUpDto signUpDto);

    String[] login(LoginDto loginDto);

    String refresh(String refreshToken);
}
