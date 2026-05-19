package com.dev.sphere.userService.clients;

import com.dev.sphere.userService.dto.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Slf4j
public class PostClientFallback implements PostClient{
    @Override
    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.warn("PostClient fallback triggered — " +
                "post-service unavailable for user with userId: {}", userId);
        return List.of(new PostDto());
    }
}
