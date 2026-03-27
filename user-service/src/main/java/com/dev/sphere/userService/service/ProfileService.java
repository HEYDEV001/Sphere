package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.ProfileRequestDto;
import com.dev.sphere.userService.dto.ProfileResponseDto;
import com.dev.sphere.userService.dto.UpdateProfileRequestDto;

public interface ProfileService {
    ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto, Long userId);

    ProfileResponseDto getProfile(Long userId);

    ProfileResponseDto updateProfileInfo(UpdateProfileRequestDto updateProfileRequestdto, Long userId);
}
