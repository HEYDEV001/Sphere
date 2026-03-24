package com.dev.sphere.connection_service.repository;

import com.dev.sphere.connection_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {


    @Query("""
            MATCH (personA:Person)-[:CONNECTED_TO]-(personB:Person)
            WHERE personA.userId = $userId
            OPTIONAL MATCH (personB)-[r]-()
            RETURN personB, collect(r) AS rels
            """)
    List<Person> getFirstDegreeConnections(Long userId);


    @Query("""
            MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
            WHERE p1.userId = $senderId AND p2.userId = $receiverId
            RETURN count(r) > 0
            """)
    Boolean connectionRequestExists(Long senderId, Long receiverId);


    @Query("""
            MATCH (p1:Person)-[r:CONNECTED_TO]-(p2:Person)
            WHERE p1.userId = $senderId AND p2.userId = $receiverId
            RETURN count(r) > 0
            """)
    Boolean alreadyConnected(Long senderId, Long receiverId);

    @Query("""
            MATCH (p1:Person), (p2:Person)
            WHERE p1.userId = $senderId AND p2.userId = $receiverId
            CREATE (p1)-[:REQUESTED_TO]->(p2)
            """)
    void sendConnectionRequest(Long senderId, Long receiverId);

    @Query("""
            MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
            WHERE p1.userId = $senderId AND p2.userId = $receiverId
            DELETE r
            CREATE (p1)-[:CONNECTED_TO]->(p2)
            """)
    void acceptConnectionRequest(Long senderId, Long receiverId);

    @Query("""
            MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
            WHERE p1.userId = $senderId AND p2.userId = $receiverId
            DELETE r
            """)
    void rejectConnectionRequest(Long senderId, Long receiverId);
}
