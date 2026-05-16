package com.dev.sphere.postService.service;

import com.dev.sphere.postService.auth.UserContextHolder;
import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.entity.PostLike;
import com.dev.sphere.postService.event.PostLikedEvent;
import com.dev.sphere.postService.exception.BadRequestException;
import com.dev.sphere.postService.exception.ResourceNotFoundException;
import com.dev.sphere.postService.repository.LikesRepository;
import com.dev.sphere.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LIKES_CACHE_PREFIX = "sphere:post:likes:";
    @Value("${flush.threshold}")
    private int FLUSH_THRESHOLD;

    private String likesKey(Long postId) {
        return LIKES_CACHE_PREFIX + postId;
    }

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

        String key = likesKey(postId);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, String.valueOf(post.getLikesCount()));
            log.info("Seeded Redis for post with Id {} with count {}", postId, post.getLikesCount());
        }
        Long newCount = redisTemplate.opsForValue().increment(key);
        log.info("Redis INCR for post {}: new count = {}", postId, newCount);


        if(newCount != null && newCount % FLUSH_THRESHOLD ==0){
            flushLikesToDb(postId, newCount);
        }


        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                .creatorId(post.getUserId())
                .likedByUserId(userId)
                .postId(postId)
                .build();

        kafkaTemplate.send("post-Liked-topic", postId, postLikedEvent);

        log.info("Liked post with id: {}", postId);

    }



    @Override
    @Transactional
    public void unlikePost(Long postId) {

        log.info(" Attempting to UnLike post with id: {}", postId);
        Long userId = UserContextHolder.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with Id : " + postId));

        boolean exists = postRepository.existsById(postId);
        if (!exists) throw new ResourceNotFoundException("Post not found with Id : " + postId);

        boolean alreadyLiked = likesRepository.existsByUserIdAndPostId(userId, postId);
        if (!alreadyLiked)
            throw new BadRequestException("Cannot unlike post with Id : " + postId + " because it is no liked by the User with Id :" + userId);

        likesRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("Unliked post with id: {}", postId);

        String key = likesKey(postId);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, String.valueOf(post.getLikesCount()));
            log.info("Seeded Redis for post {} with count {}", postId, post.getLikesCount());
        }
        Long newCount = redisTemplate.opsForValue().decrement(key);
        log.info("Redis DECR for post {}: new count = {}", postId, newCount);


        if (newCount != null && newCount % FLUSH_THRESHOLD == 0) {
            flushLikesToDb(postId, newCount);
        }

        log.info("Unliked post with id: {}", postId);
    }


    private void flushLikesToDb(Long postId, Long redisCount) {
        log.info("Flushing likes count to DB for post {}: count = {}", postId, redisCount);
        postRepository.findById(postId).ifPresent(post -> {
            post.setLikesCount(redisCount);
            postRepository.save(post);
            log.info("DB flushed for post {}: likesCount = {}", postId, redisCount);
        });
    }
}
