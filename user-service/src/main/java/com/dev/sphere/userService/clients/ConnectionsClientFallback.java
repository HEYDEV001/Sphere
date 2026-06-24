package com.dev.sphere.userService.clients;// com.dev.sphere.userService.clients.ConnectionsClientFallback

import com.dev.sphere.userService.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConnectionsClientFallback implements ConnectionsClient {

    @Override
    public ResponseEntity<PersonDto> createPerson(PersonDto personDto) {
        log.warn("Fallback — connection-service unavailable, person not created for userId: {}",
                personDto.getUserId());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}