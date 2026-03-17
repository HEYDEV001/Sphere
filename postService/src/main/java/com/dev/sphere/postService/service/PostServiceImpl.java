package com.dev.sphere.postService.service;

import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.exception.ResourceNotFoundException;
import com.dev.sphere.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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

    @Override
    public PostDto getPostById(Long postId) {
        log.info("Get post by id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post Not found with Id: "+postId));
        log.info("Got The post by id: {}", post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all posts of user: {}", userId);
        List<Post> allPosts = postRepository.getAllByUserId(userId);
        log.info("retuning all posts of user: {}", allPosts);
        return allPosts.stream()
                .map((posts)-> modelMapper.map(posts, PostDto.class))
                .collect(Collectors.toList());
    }
}
