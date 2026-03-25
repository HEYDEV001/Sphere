package com.dev.sphere.userService.clients;

import com.dev.sphere.userService.dto.PostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "post-service",path = "/posts")
public interface PostClient {

    @GetMapping("/core/users/userPosts/profilePost/{userId}")
    List<PostDto> getAllPostsOfUser(@PathVariable Long userId);
}
