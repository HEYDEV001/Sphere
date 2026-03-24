package com.dev.sphere.notification_service.consumer;

import com.dev.sphere.notification_service.clients.ConnectionsClient;
import com.dev.sphere.notification_service.dto.PersonDto;
import com.dev.sphere.notification_service.entity.Notification;
import com.dev.sphere.notification_service.repository.NotificationRepository;
import com.dev.sphere.notification_service.service.SendNotification;
import com.dev.sphere.postService.event.PostCreatedEvent;
import com.dev.sphere.postService.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final SendNotification sendNotification;
    @KafkaListener(topics = "post-created-topic")
    public void handelPostCreated(PostCreatedEvent postCreatedEvent) {
        log.info("Sending Notification for postCreated event: {}", postCreatedEvent);
        List<PersonDto> firstDegreeConnections= connectionsClient.getFirstConnections(postCreatedEvent.getCreatorId());
        for (PersonDto connection : firstDegreeConnections) {

            sendNotification.send(connection.getUserId(),
                    "Your Connection " + postCreatedEvent.getCreatorId()+"created a post, Check it Out");
        }

    }

    @KafkaListener(topics = "post-Liked-topic")
    public void handelPostLiked(PostLikedEvent postLikedEvent) {
        log.info("Sending Notification for postLiked event: {}", postLikedEvent);
        String message = String.format("you post %d has been liked by %d"
                , postLikedEvent.getPostId(), postLikedEvent.getLikedByUserId());

        sendNotification.send(postLikedEvent.getCreatorId(),message);
        log.info("Post has been liked by user with Id: {}", postLikedEvent.getLikedByUserId());
    }




}
