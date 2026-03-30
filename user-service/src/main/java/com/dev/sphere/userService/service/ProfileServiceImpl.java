package com.dev.sphere.userService.service;

import com.dev.sphere.userService.clients.PostClient;
import com.dev.sphere.userService.dto.*;
import com.dev.sphere.userService.entity.Profile;
import com.dev.sphere.userService.entity.User;
import com.dev.sphere.userService.exception.ResourceNotFoundException;
import com.dev.sphere.userService.repository.ProfileRepository;
import com.dev.sphere.userService.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PostClient postClient;
    private final HttpServletRequest httpServletRequest;
    private final JwtService jwtService;

    @Override
    public List<SearchResponseDto> searchProfile(SearchRequestDto searchRequestDto) {
        String name  = searchRequestDto.getName();
        List<Profile> profiles = profileRepository.findByNameContainingIgnoreCase(name);
        return profiles.stream()
                .map((profile)-> modelMapper.map(profile, SearchResponseDto.class))
                .toList();
    }

    @Transactional
    @Override
    public ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto) {

       String header =  httpServletRequest.getHeader("Authorization");
       if(header == null || !header.startsWith("Bearer ")) {
           throw new JwtException("Invalid token");
       }
       String token = header.substring(7);
       Long userId  = jwtService.getIdFromTheToken(token);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));

        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(user.getName());
        profile.setEmail(user.getEmail());
        profile.setEducation(profileRequestDto.getEducation());
        profile.setSkills(profileRequestDto.getSkills());
        profile.setExperience(profileRequestDto.getExperience());
        profile.setProfilePicture(profileRequestDto.getProfilePicture());
        profile.setDescription(profileRequestDto.getDescription());

        profileRepository.save(profile);

        return modelMapper.map(profile, ProfileResponseDto.class);
    }

    @Override
    public GetProfileResponseDto getProfile(Long userId) {
        log.info("Get profile by id: {}", userId);
        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        GetProfileResponseDto response =  modelMapper.map(profile, GetProfileResponseDto.class);
        List<PostDto> postDtoList = postClient.getAllPostsOfUser(userId);

      response.setPosts(postDtoList);
      return response;
    }

    @Transactional
    @Override
    public UpdatedProfileResponseDto updateProfileInfo(UpdateProfileRequestDto updateProfileRequestdto) {
        String header =  httpServletRequest.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            throw new JwtException("Invalid token");
        }
        String token = header.substring(7);
        Long userId  = jwtService.getIdFromTheToken(token);

        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        profile.setName(updateProfileRequestdto.getName());
        profile.setProfilePicture(updateProfileRequestdto.getProfilePicture());
        profile.setDescription(updateProfileRequestdto.getDescription());
        profileRepository.save(profile);
        return modelMapper.map(profile, UpdatedProfileResponseDto.class);
    }
}
