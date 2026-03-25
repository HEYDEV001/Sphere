package com.dev.sphere.postService.repository;

import com.dev.sphere.postService.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getAllByUserId(Long userId);


    Post findByIdAndUserId(Long postId, Long userId);
}
