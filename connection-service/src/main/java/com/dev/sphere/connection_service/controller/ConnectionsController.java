package com.dev.sphere.connection_service.controller;

import com.dev.sphere.connection_service.annotation.NoWrap;
import com.dev.sphere.connection_service.entity.Person;
import com.dev.sphere.connection_service.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    @NoWrap 
    @GetMapping("/first-degree")
    public ResponseEntity<List<Person>> getMyFirstConnections() {
        return ResponseEntity.ok(connectionsService.getFirstDegreeConnection());
    }
    
    @GetMapping("/you-may-know")
    public ResponseEntity<List<Person>> getYouMayKnowConnections() {
        return ResponseEntity.ok(connectionsService.getYouMayKnowConnections());
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<Boolean> sendConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.sendConnectionRequest(userId));
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Boolean> acceptConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.acceptConnectionRequest(userId));
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Boolean> rejectConnectionRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionsService.rejectConnectionRequest(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity.ok(connectionsService.createPerson(person));
    }
}
