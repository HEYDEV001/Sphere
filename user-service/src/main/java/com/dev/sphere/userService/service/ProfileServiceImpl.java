package com.dev.sphere.userService.service;

import com.dev.sphere.userService.auth.UserContextHolder;
import com.dev.sphere.userService.clients.PostClient;
import com.dev.sphere.userService.dto.PostDto;
import com.dev.sphere.userService.dto.ProfileRequestDto;
import com.dev.sphere.userService.dto.ProfileResponseDto;
import com.dev.sphere.userService.entity.Post;
import com.dev.sphere.userService.entity.Profile;
import com.dev.sphere.userService.entity.User;
import com.dev.sphere.userService.exception.ResourceNotFoundException;
import com.dev.sphere.userService.repository.ProfileRepository;
import com.dev.sphere.userService.repository.UserRepository;
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

    @Transactional
    @Override
    public ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto, Long userId) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found"));

        List<PostDto> postDtoList = postClient.getAllPostsOfUser(userId);
        List<Post> posts = postDtoList.stream()
                .map((post)-> modelMapper.map(post, Post.class) )
                .toList();

        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(user.getName());
        profile.setEmail(user.getEmail());
        profile.setProfilePicture(profileRequestDto.getProfilePicture());
        profile.setDescription(profileRequestDto.getDescription());
        profile.setPosts(posts);

        profileRepository.save(profile);

        return modelMapper.map(profile, ProfileResponseDto.class);
    }

    @Override
    public ProfileResponseDto getProfile(Long userId) {
        log.info("Get profile by id: {}", userId);
        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        ProfileResponseDto response =  modelMapper.map(profile, ProfileResponseDto.class);
        List<PostDto> postDtoList = postClient.getAllPostsOfUser(userId);

      response.setPosts(postDtoList);
      return response;
    }
}
