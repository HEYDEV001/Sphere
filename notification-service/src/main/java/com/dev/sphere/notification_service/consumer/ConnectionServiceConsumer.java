package com.dev.sphere.notification_service.consumer;


import com.dev.sphere.connection_service.event.AcceptConnectionRequestEvent;
import com.dev.sphere.connection_service.event.SendConnectionRequestEvent;
import com.dev.sphere.notification_service.service.SendNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionServiceConsumer {
    private final SendNotification sendNotification;


    @KafkaListener(topics = "send-connection-topic")
    public void handelSendConnectionRequest(SendConnectionRequestEvent sendConnectionRequestEvent) {
        log.info("Sending connection request: {}", sendConnectionRequestEvent);
        String message = String.format("you have received a connection request from a user with Id %d",sendConnectionRequestEvent.getSenderId());
        sendNotification.send(sendConnectionRequestEvent.getReceiverId(), message);

    }

    @KafkaListener(topics = "accept-connection-topic")
    public void handelAcceptConnectionRequest(AcceptConnectionRequestEvent acceptConnectionRequestEvent) {
        log.info("Accepting connection request: {}", acceptConnectionRequestEvent);
        String message = String.format("your request has been accepted by the user with Id %d",acceptConnectionRequestEvent.getReceiverId());
        sendNotification.send(acceptConnectionRequestEvent.getSenderId(), message);

    }
}
