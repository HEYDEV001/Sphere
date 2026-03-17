package com.dev.sphere.postService.controller;

import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import com.dev.sphere.postService.repository.PostRepository;
import com.dev.sphere.postService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        PostDto createdPost = postService.createPost(postRequestDto, 1L);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
}
