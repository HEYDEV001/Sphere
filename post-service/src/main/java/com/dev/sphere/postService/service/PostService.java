package com.dev.sphere.postService.service;

import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;

import java.util.List;


public interface PostService {
    PostDto createPost(PostRequestDto postRequestDto);

    PostDto getPostById(Long postId);

    List<PostDto> getAllMyPosts(Long userId);

    Boolean deletePost(Long postId);

    List<PostDto> getAllPostsOfUser(Long userId);
}
