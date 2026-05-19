package com.dev.sphere.userService.clients;

import com.dev.sphere.userService.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "connection-service",path = "/connections")
public interface ConnectionsClient {
    @PostMapping("/core/create")
    ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto);
}
