package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.*;

import java.util.List;

public interface ProfileService {
    ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto);

    GetProfileResponseDto getProfile(Long userId);

    UpdatedProfileResponseDto updateProfileInfo(UpdateProfileRequestDto updateProfileRequestdto);

    List<SearchResponseDto> searchProfile(SearchRequestDto searchRequestDto);
}
