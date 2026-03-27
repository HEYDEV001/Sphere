package com.dev.sphere.userService.controller;

import com.dev.sphere.userService.auth.UserContextHolder;
import com.dev.sphere.userService.dto.ProfileRequestDto;
import com.dev.sphere.userService.dto.ProfileResponseDto;
import com.dev.sphere.userService.dto.UpdateProfileRequestDto;
import com.dev.sphere.userService.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;


    @PostMapping("/create/{userId}")
    public ResponseEntity<ProfileResponseDto> createProfile(@RequestBody ProfileRequestDto profileRequestDto, @PathVariable Long userId) {
        log.info("creating the profile for user: {}", userId);
        return ResponseEntity.ok(profileService.createProfile(profileRequestDto,userId));
    }

    @PostMapping("/getProfile/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long userId) {
        log.info("getting the profile for user: {}",userId);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ProfileResponseDto> updateProfileInfo(@PathVariable Long userId, @RequestBody UpdateProfileRequestDto updateProfileRequestdto){
        log.info("updating the profile for user: {}",userId);
        return ResponseEntity.ok(profileService.updateProfileInfo(updateProfileRequestdto, userId));
    }
}
