package com.dev.sphere.postService.event;

import com.dev.sphere.postService.auth.UserContextHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {
    Long creatorId ;
    String content;
    Long postId;
}
