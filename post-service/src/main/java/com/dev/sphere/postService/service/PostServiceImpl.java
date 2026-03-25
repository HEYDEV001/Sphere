package com.dev.sphere.postService.service;

import com.dev.sphere.postService.auth.UserContextHolder;
import com.dev.sphere.postService.clients.ConnectionsClient;
import com.dev.sphere.postService.dto.PersonDto;
import com.dev.sphere.postService.dto.PostDto;
import com.dev.sphere.postService.dto.PostRequestDto;
import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.event.PostCreatedEvent;
import com.dev.sphere.postService.exception.ResourceNotFoundException;
import com.dev.sphere.postService.exception.UnAuthorizedException;
import com.dev.sphere.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    @Override
    public PostDto createPost(PostRequestDto postRequestDto) {
        Long userId = UserContextHolder.getCurrentUser();
        Post newPost = modelMapper.map(postRequestDto, Post.class);
        newPost.setUserId(userId);
        newPost.setLikesCount(0L);
        log.info("Create new post: {}", newPost);
        Post savedPost = postRepository.save(newPost);
        log.info("Saved post: {}", savedPost);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                .creatorId(userId)
                .postId(savedPost.getId())
                .content(savedPost.getContent())
                .build();

        kafkaTemplate.send("post-created-topic", postCreatedEvent);
        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    public PostDto getPostById(Long postId) {
        log.info("Get post by id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post Not found with Id: " + postId));
        log.info("Got The post by id: {}", post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> getAllMyPosts(Long userId) {
        log.info("Getting all posts of user: {}", userId);
        List<Post> allPosts = postRepository.getAllByUserId(userId);
        log.info("retuning all posts of user: {}", allPosts);
        return allPosts.stream()
                .map((posts) -> modelMapper.map(posts, PostDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Boolean deletePost(Long postId) {
        log.info("Trying to delete the post with Id : {}", postId);
        Long userId = UserContextHolder.getCurrentUser();
        Post post = postRepository.findByIdAndUserId(postId, userId);
        if(post==null) {
            throw new UnAuthorizedException("Post does not belong to the current user with Id: " + userId);
        }
        postRepository.deleteById(postId);
        return true;
    }

    @Override
    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all posts of user with Id: {}", userId);
        List<Post> allPosts = postRepository.getAllByUserId(userId);
        log.info("retuning all posts of user with Id: {}", allPosts);
        return allPosts.stream()
                .map((posts) -> modelMapper.map(posts, PostDto.class))
                .collect(Collectors.toList());
    }


}
