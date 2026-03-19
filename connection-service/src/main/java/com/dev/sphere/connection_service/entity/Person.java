package com.dev.sphere.connection_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;


@Setter
@Getter
@Node("Person")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String name;

}
