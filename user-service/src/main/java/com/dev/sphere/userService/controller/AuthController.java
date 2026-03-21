package com.dev.sphere.userService.controller;

import com.dev.sphere.userService.dto.LoginDto;
import com.dev.sphere.userService.dto.LoginResponseDto;
import com.dev.sphere.userService.dto.SignUpDto;
import com.dev.sphere.userService.dto.UserDto;
import com.dev.sphere.userService.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto) {
        UserDto userDto = authService.signUp(signUpDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request,
                                          HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        return ResponseEntity.ok(loginResponseDto);
    }
}
