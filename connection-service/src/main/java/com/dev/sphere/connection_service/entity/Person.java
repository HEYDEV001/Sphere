package com.dev.sphere.connection_service.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Person {
    @Id
    @GeneratedValue
    private Long Id;

    private Long userID;

    private String name;
}
