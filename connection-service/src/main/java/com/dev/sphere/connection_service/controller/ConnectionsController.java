package com.dev.sphere.connection_service.controller;

import com.dev.sphere.connection_service.entity.Person;
import com.dev.sphere.connection_service.service.ConnectionsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>> getFirstConnections(@PathVariable Long userId) {
        return new ResponseEntity<>( connectionsService.getFirstDegreeConnection(userId), HttpStatus.FOUND);
    }
    @GetMapping("/{userId}/second-degree")
    public ResponseEntity<List<Person>> getSecondConnections(@PathVariable Long userId) {
        return new ResponseEntity<>( connectionsService.getSecondDegreeConnection(userId), HttpStatus.FOUND);
    }

}
