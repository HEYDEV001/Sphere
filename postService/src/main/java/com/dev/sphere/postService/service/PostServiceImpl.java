package com.dev.sphere.postService.service;

import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDto createPost(PostRequestDto postRequestDto, Long userId) {
        Post newPost = modelMapper.map(postRequestDto, Post.class);
        newPost.setUserId(userId);
        log.info("Create new post: {}", newPost);
        Post savedPost = postRepository.save(newPost);
        log.info("Saved post: {}", savedPost);
        return modelMapper.map(savedPost, PostDto.class);
    }
}
