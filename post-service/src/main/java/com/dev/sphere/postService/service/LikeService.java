package com.dev.sphere.postService.service;

public interface LikeService {
    public void likePost(Long postId);

    void unlikePost(Long postId);
}
