package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.*;

import java.util.List;

public interface ProfileService {
    ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto, Long userId);

    ProfileResponseDto getProfile(Long userId);

    UpdatedProfileResponseDto updateProfileInfo(UpdateProfileRequestDto updateProfileRequestdto, Long userId);

    List<UpdatedProfileResponseDto> searchProfile(SearchRequestDto searchRequestDto);
}
