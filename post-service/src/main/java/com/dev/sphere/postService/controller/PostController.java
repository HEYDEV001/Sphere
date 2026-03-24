package com.dev.sphere.postService.controller;

import com.dev.sphere.postService.auth.UserContextHolder;
import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import com.dev.sphere.postService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequestDto postRequestDto) {
        PostDto createdPost = postService.createPost(postRequestDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        log.info("Getting post by id: {}", postId);
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PostMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId) {
        log.info("Getting all posts by user id: {}", userId);
        List<PostDto> allPosts = postService.getAllPostsOfUser(userId);
        return ResponseEntity.ok(allPosts);
    }

}
