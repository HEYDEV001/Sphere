package com.dev.sphere.userService.controller;

import com.dev.sphere.userService.dto.*;
import com.dev.sphere.userService.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;


    @PostMapping("/create")
    public ResponseEntity<ProfileResponseDto> createProfile(@RequestBody ProfileRequestDto profileRequestDto, HttpServletRequest httpServletRequest) {
        log.info("creating the profile for user: {}",httpServletRequest );
        return ResponseEntity.ok(profileService.createProfile(profileRequestDto));
    }

    @PostMapping("/getProfile/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long userId) {
        log.info("getting the profile for user: {}",userId);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<UpdatedProfileResponseDto> updateProfileInfo( @RequestBody UpdateProfileRequestDto updateProfileRequestdto, HttpServletRequest httpServletRequest){
        log.info("updating the profile for user: {}",httpServletRequest);
        return ResponseEntity.ok(profileService.updateProfileInfo(updateProfileRequestdto));
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResponseDto>> searchProfile(@RequestBody SearchRequestDto searchRequestDto){
        log.info("searching the profile for users with name similar to : {}",searchRequestDto);
        return ResponseEntity.ok(profileService.searchProfile(searchRequestDto));
    }

}
