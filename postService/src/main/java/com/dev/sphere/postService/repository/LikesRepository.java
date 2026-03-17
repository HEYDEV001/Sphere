package com.dev.sphere.postService.repository;

import com.dev.sphere.postService.entity.Post;
import com.dev.sphere.postService.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface LikesRepository extends JpaRepository<PostLike, Long> {

    Boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(long userId, Long postId);
}
