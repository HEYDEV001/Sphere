package com.dev.sphere.postService.service;

public interface LikeService {
    public void likePost(Long postId, Long userId);

    void unlikePost(Long postId, long UserId);
}
