//package com.dev.sphere.userService.clients;
//import com.dev.sphere.userService.dto.PersonDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//
//@Component
//@Slf4j
//public class ConnectionsClientFallback implements ConnectionsClient {
//
//
//    @Override
//    public ResponseEntity<PersonDto> createPerson(PersonDto personDto) {
//        log.warn("ConnectionsClient fallback triggered — " +
//                "connection-service unavailable for person: {}", personDto);
//        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
//    }
//}