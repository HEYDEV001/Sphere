package com.dev.sphere.connection_service.service;

import com.dev.sphere.connection_service.entity.Person;
import com.dev.sphere.connection_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {


    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnection(Long userId) {
        log.info("get First Degree Connection for the User with Id: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

    public List<Person> getSecondDegreeConnection(Long userId) {
        log.info("get Second Degree Connection for the User with Id: {}", userId);
        return personRepository.getSecondDegreeConnections(userId);
    }
}
