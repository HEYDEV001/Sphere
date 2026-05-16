package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.*;

import java.util.List;
import java.util.Map;

public interface ProfileService {
    ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto);

    GetProfileResponseDto getProfile(Long userId);


    SearchResultsWrapper searchProfile(SearchRequestDto searchRequestDto);

    UpdatedProfileResponseDto updateProfileInfo(Map<String, Object> updates);
}
