package com.dev.sphere.postService.service;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.PositiveIntegerType;
import com.dev.sphere.postService.auth.UserContextHolder;
import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.entity.PostLike;
import com.dev.sphere.postService.event.PostLikedEvent;
import com.dev.sphere.postService.exception.BadRequestException;
import com.dev.sphere.postService.exception.ResourceNotFoundException;
import com.dev.sphere.postService.repository.LikesRepository;
import com.dev.sphere.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {


    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId) {
        log.info(" Attempting to Like post with id: {}", postId);
        Long userId = UserContextHolder.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with Id : " + postId));

        boolean alreadyLiked = likesRepository.existsByUserIdAndPostId(userId, postId);
        if (alreadyLiked)
            throw new BadRequestException("Post already liked with Id : " + postId + ", same post can not be liked again");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        likesRepository.save(postLike);
        log.info(" Liked post with id: {}", postId);

        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                .creatorId(post.getUserId())
                .likedByUserId(userId)
                .postId(postId).build();

        kafkaTemplate.send("post-Liked-topic", postId, postLikedEvent);


    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {

        log.info(" Attempting to UnLike post with id: {}", postId);
        Long userId = UserContextHolder.getCurrentUser();
        boolean exists = postRepository.existsById(postId);
        if (!exists) throw new ResourceNotFoundException("Post not found with Id : " + postId);

        boolean alreadyLiked = likesRepository.existsByUserIdAndPostId(userId, postId);
        if (!alreadyLiked)
            throw new BadRequestException("Cannot unlike post with Id : " + postId + " because it is no liked by the User with Id :" + userId);

        likesRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("Unliked post with id: {}", postId);
    }

}
