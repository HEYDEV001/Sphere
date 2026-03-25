package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.ProfileRequestDto;
import com.dev.sphere.userService.dto.ProfileResponseDto;

public interface ProfileService {
    ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto, Long userId);

    ProfileResponseDto getProfile(Long userId);
}
