package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.ForgotPasswordRequestDto;
import com.dev.sphere.userService.dto.ResetPasswordRequestDto;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequestDto requestDto);

    void resetPassword(ResetPasswordRequestDto requestDto);
}
