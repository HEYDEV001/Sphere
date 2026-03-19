package com.dev.sphere.connection_service.repository;

import com.dev.sphere.connection_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {
    Optional<Person> getByName(String name);

    @Query("""
            MATCH (personA:Person)-[:CONNECTED_TO]-(personB:Person)
            WHERE personA.userId = $userId
            OPTIONAL MATCH (personB)-[r]-()
            RETURN personB, collect(r) AS rels
            """)
    List<Person> getFirstDegreeConnections(Long userId);

    @Query("MATCH (personA:Person) -[:CONNECTED_TO]- (personB:Person)" +
            " WHERE personA.userId = $userId " +
            "RETURN personB")
        //TODO: Change the Query to get the second degree connections
    List<Person> getSecondDegreeConnections(Long userId);
}
