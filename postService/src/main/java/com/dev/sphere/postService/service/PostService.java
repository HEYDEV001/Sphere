package com.dev.sphere.postService.service;

import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import org.springframework.stereotype.Service;


public interface PostService {
    PostDto createPost(PostRequestDto postRequestDto, Long userId);
}
