# Sphere

> A LinkedIn-like professional networking platform built with Spring Boot microservices, API Gateway, service discovery, and containerized deployment.

---

## Project Objective

Sphere is a backend-focused professional networking platform designed to replicate the core functionality of LinkedIn. The goal of this project was to build a production-grade, distributed backend system using modern microservices architecture — covering real-world concerns like asynchronous event-driven communication, inter-service coordination, graph-based relationship modeling, and secure JWT authentication — all containerized and orchestrated through a unified API Gateway.

This project was built to deepen practical understanding of microservices design patterns, distributed systems challenges, and the Spring Boot ecosystem at scale.

---

## Architecture Overview

```
Client
  │
  ▼
API Gateway  (port 8080)
  │   JWT Authentication Filter · Route-based authorization · Load balancing
  │
  ├──▶ User Service          → Profile management, authentication, registration
  ├──▶ Post Service          → Create, read, update, delete posts · Feed
  ├──▶ Connection Service    → Follow/unfollow · Connection graph (Neo4j)
  └──▶ Notification Service  → Event-driven alerts via Kafka
  
  All services register with Eureka Discovery Server
  Services communicate via Feign Client (sync) and Kafka (async)
```

---

## Project Structure

```
Sphere/
├── api-gateway/                  # Spring Cloud Gateway — routing, JWT filter
├── discovery-server/             # Netflix Eureka — service registry
├── user-service/                 # User auth, profiles, registration
├── post-service/                 # Post CRUD, feed
├── connection-service/           # Follow graph, Neo4j
├── notification-service/         # Kafka consumer, event-driven notifications
└── docker-compose.yml            # Containerized local deployment
```

### Service Responsibilities

| Service | Responsibility | Database |
|---|---|---|
| `api-gateway` | JWT validation, request routing, load balancing | — |
| `discovery-server` | Service registration and discovery (Eureka) | — |
| `user-service` | Registration, login, JWT issuance, user profiles | PostgreSQL |
| `post-service` | Post creation, retrieval, feed | PostgreSQL |
| `connection-service` | Follow/unfollow, connection graph traversal | Neo4j |
| `notification-service` | Consumes Kafka events, sends notifications | PostgreSQL |

---

## Key Features

- **JWT-based Authentication** — Stateless token authentication with a custom filter at the gateway level. All protected routes validate the token before forwarding requests downstream.
- **API Gateway** — Single entry point for all client requests. Routes traffic to the correct service, strips path prefixes, and enforces authentication via a custom `AbstractGatewayFilterFactory`.
- **Service Discovery** — All microservices register with Netflix Eureka. The gateway performs client-side load balancing using `lb://` URIs.
- **Asynchronous Event-Driven Notifications** — Apache Kafka decouples event producers (user-service, post-service) from the notification-service consumer. Events like follows, likes, and comments trigger real-time notifications without direct service coupling.
- **Graph-based Connection System** — Neo4j powers the connection graph, enabling efficient relationship traversal — following, followers, and mutual connection queries that would be expensive in a relational database.
- **Inter-service Communication via Feign Client** — Services communicate synchronously using declarative Feign clients, enabling clean, typed HTTP calls between microservices without boilerplate `RestTemplate` code.
- **Containerized Deployment** — All services and infrastructure (Kafka, Zookeeper, PostgreSQL, Neo4j, Eureka) are containerized and orchestrated with Docker Compose for consistent local development and deployment.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka (Spring Cloud Netflix) |
| Inter-service Calls | OpenFeign (Spring Cloud OpenFeign) |
| Async Messaging | Apache Kafka |
| Auth | Custom JWT filter (jjwt) |
| Databases | PostgreSQL · Neo4j |
| ORM | Spring Data JPA · Spring Data Neo4j |
| Containerization | Docker · Docker Compose |
| Build Tool | Maven |

---

## Challenges Faced & How I Solved Them

### 1. Setting Up Apache Kafka — Local and In-Code

**Challenge:** Kafka was one of the most difficult parts of this project. Setting it up locally required configuring Zookeeper, the Kafka broker, topics, and producer/consumer properties — none of which has a simple plug-and-play setup. Beyond the local environment, integrating Kafka into Spring Boot involved understanding serialization strategies, consumer group behavior, deserialization of custom event objects across services, and handling `JsonDeserializer` trusted package configuration.

**How I solved it:** I read through the official Apache Kafka documentation and the Spring Kafka reference guide extensively. I debugged serialization mismatches, resolved `ClassNotFoundException` issues caused by package path differences between producer and consumer services, and settled on a consistent `application.yml`-based configuration approach across all services — eliminating manual `@Bean` configs in favor of Spring Boot's auto-configuration.

**Scalability improvement:** By using Kafka for all asynchronous communication, the notification-service is fully decoupled from all producers. Adding a new event type (e.g. comment notifications, connection request alerts) requires only a new Kafka topic and a new `@KafkaListener` — no changes to any producer service.

---

### 2. Inter-service Communication via Feign Client

**Challenge:** Feign client integration caused persistent issues across multiple services. Problems included mismatched response types between services, incorrect path mappings after `StripPrefix` at the gateway, Feign clients incorrectly routing through the gateway instead of directly to services, and unclear error messages when a downstream service returned an unexpected response body.

**How I solved it:** I systematically debugged each Feign client by testing the target service endpoints directly via Postman first, then aligning the Feign interface method signatures exactly with the controller signatures of the target service. I learned that Feign clients in a microservices setup should use the Eureka service name (e.g. `USER-SERVICE`) and bypass the gateway entirely for internal calls — which resolved routing and auth header conflicts.

**Scalability improvement:** All inter-service calls are now defined as clean, typed Feign interfaces. Adding a new cross-service dependency requires only a new interface method — no manual HTTP client setup, no URL management.

---

### 3. JWT Propagation Across Services

**Challenge:** After the gateway validates the JWT and extracts the `userId`, downstream services needed access to that user identity without re-validating the token. Getting the gateway to forward the `userId` as a request header and ensuring Feign clients preserved that header in downstream calls required careful filter and interceptor configuration.

**How I solved it:** The custom `AuthenticationFilter` in the gateway mutates the outbound request to add a `userId` header before forwarding. Each downstream service reads this header directly from the request — no token re-validation needed. Feign clients propagate the header using a `RequestInterceptor`.

---

### 4. Graph Modeling for Connections in Neo4j

**Challenge:** Modeling professional connections (follow/unfollow, mutual connections, suggested people) in a relational database would require complex join queries that degrade at scale. Choosing Neo4j introduced a learning curve around Cypher query language, Spring Data Neo4j entity modeling, and relationship direction semantics.

**How I solved it:** Modeled users as `@Node` entities and connections as typed `@Relationship` properties with direction. Cypher queries for "people you may know" become simple 2-hop traversals — something that would require multiple joins and subqueries in SQL.

**Scalability improvement:** The graph model scales naturally with the number of connections. Traversal queries remain performant even with millions of relationships — exactly the use case Neo4j is designed for.

---

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

### Run locally

```bash
# Clone the repository
git clone https://github.com/HEYDEV001/Sphere.git
cd Sphere

# Set environment variables
export JWT_SECRET=your_secret_key_here
export MAIL_USERNAME=youremail@gmail.com
export MAIL_PASSWORD=your_app_password

# Start all infrastructure and services
docker-compose up --build
```

### Service Ports

| Service | Port |
|---|---|
| API Gateway | 8080 |
| Discovery Server (Eureka UI) | 8761 |
| User Service | 8081 |
| Post Service | 8082 |
| Connection Service | 8083 |
| Notification Service | 8084 |

### Example API Calls

```bash
# Register
POST http://localhost:8080/api/v1/user/register

# Login
POST http://localhost:8080/api/v1/user/login

# Create a post (authenticated)
POST http://localhost:8080/api/v1/posts
Authorization: Bearer <your_jwt_token>

# Follow a user (authenticated)
POST http://localhost:8080/api/v1/connections/follow/{userId}
Authorization: Bearer <your_jwt_token>
```

---

## What's Coming Next

- [ ] Password reset flow (email-based token)
- [ ] Direct messaging service (WebSocket + Kafka)
- [ ] Full-text search (PostgreSQL FTS / Elasticsearch)
- [ ] Post reactions and comments
- [ ] "People you may know" recommendations (Neo4j 2-hop traversal)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Centralized configuration (Spring Cloud Config)
- [ ] Distributed tracing (Micrometer + Zipkin)

---

## Author

**HEYDEV001**
[github.com/HEYDEV001](https://github.com/HEYDEV001)

---

> Built in 12 days. Every service, every Kafka topic, every Feign client — from scratch.
