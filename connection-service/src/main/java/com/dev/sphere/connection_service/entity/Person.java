package com.dev.sphere.connection_service.entity;

import com.netflix.discovery.provider.Serializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.io.Serializable;


@Setter
@Getter
@Node("Person")
public class Person implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String name;

}
