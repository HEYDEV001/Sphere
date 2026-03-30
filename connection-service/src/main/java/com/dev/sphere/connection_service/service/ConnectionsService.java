package com.dev.sphere.connection_service.service;

import com.dev.sphere.connection_service.auth.UserContextHolder;
import com.dev.sphere.connection_service.entity.Person;
import com.dev.sphere.connection_service.event.AcceptConnectionRequestEvent;
import com.dev.sphere.connection_service.event.SendConnectionRequestEvent;
import com.dev.sphere.connection_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {


    private final PersonRepository personRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendConnectionRequestKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptConnectionRequestKafkaTemplate;


    public List<Person> getFirstDegreeConnection() {
        Long userId = UserContextHolder.getCurrentUser();
        log.info("get First Degree Connection for the User with Id: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

    public Boolean sendConnectionRequest(Long receiverId) {
        log.info("Getting Sender's Id From the UserContextHolder");
        Long senderId = UserContextHolder.getCurrentUser();

        if (senderId == receiverId) {
            throw new RuntimeException("Sender Id and Receiver Id are the same, you can not send request to yourself");
        }

        log.info("Checking if the request Already exist or not");
        Boolean requestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if (requestExists) {
            throw new RuntimeException("Connection Request already exists");
        }

        log.info("Checking if the Connection Already exist or not");
        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new RuntimeException("Connection already exists, can not send connection request");
        }

        log.info("Sending connection request from {} to {}", senderId, receiverId);
        personRepository.sendConnectionRequest(senderId, receiverId);

        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        sendConnectionRequestKafkaTemplate.send("send-connection-topic", sendConnectionRequestEvent);

        return true;
    }

    public Boolean acceptConnectionRequest(Long senderId) {

        Long receiverId = UserContextHolder.getCurrentUser();
        log.info("Checking if the Connection request Already exist ");
        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if (!connectionRequestExists) {
            throw new RuntimeException("Connection Request does not exist");
        }
        log.info("Accepting the connection request from {} to {}", senderId, receiverId);
        personRepository.acceptConnectionRequest(senderId, receiverId);
        log.info(" Successfully accepted the connection request from {} to {}", senderId, receiverId);
        AcceptConnectionRequestEvent acceptConnectionRequestEvent = AcceptConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        acceptConnectionRequestKafkaTemplate.send("accept-connection-topic", acceptConnectionRequestEvent);

        return true;

    }

    public Boolean rejectConnectionRequest(Long senderId) {
        log.info("rejecting connection request from {} to {}", senderId, UserContextHolder.getCurrentUser());
        Long receiverId = UserContextHolder.getCurrentUser();
        log.info("Checking if the request Already exist ");
        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if (!connectionRequestExists) {
            throw new RuntimeException("Connection Request does not exist, Can not reject connection request");
        }
        log.info("Rejecting the connection request from {} to {}", senderId, receiverId);
        personRepository.rejectConnectionRequest(senderId, receiverId);
        return true;
    }

    public Person createPerson(Person person) {
        log.info("Creating a new person with userId : {}, and name : {}", person.getUserId(), person.getName());
        return personRepository.save(person);
    }

    public List<Person> getYouMayKnowConnections() {
        Long userId = UserContextHolder.getCurrentUser();
        log.info("getting Second Degree Connection for the User with Id: {}", userId);
        return personRepository.getSecondDegreeConnections(userId);
    }
}
