# Sphere

> A LinkedIn-like professional networking platform built with Spring Boot microservices — featuring Redis caching, rate limiting, circuit breakers, distributed tracing, and centralized configuration.

---

## Project Objective

Sphere is a backend-focused professional networking platform designed to replicate the core functionality of LinkedIn. Built as a production-grade distributed system using modern microservices architecture — covering real-world concerns like asynchronous event-driven communication, inter-service resilience, graph-based relationship modeling, multi-layer caching, and secure JWT authentication — all managed through a unified API Gateway and centralized configuration server.

This project was built to develop deep practical understanding of microservices design patterns, distributed systems challenges, and the Spring Boot ecosystem at scale.

---

## Architecture Overview

```
Client
  │
  ▼
API Gateway  (port 8080)
  │   JWT auth · Rate limiting (Redis token bucket) · Circuit breaker · Routing
  │
  ├──▶ Config Server         → Centralized configuration for all services
  ├──▶ User Service          → Auth, profiles, registration, search
  ├──▶ Post Service          → Post CRUD, likes
  ├──▶ Connection Service    → Follow graph (Neo4j), people you may know
  └──▶ Notification Service  → Kafka consumer, event-driven alerts

  All services register with Eureka Discovery Server
  Services communicate via Feign Client (sync) and Kafka (async)
  Redis shared across all services for caching
  Zipkin + Micrometer for distributed tracing
```

---

## Project Structure

```
Sphere/
│
├── api-gateway/
│   └── src/main/java/com/dev/sphere/api_gateway/
│       ├── ApiGatewayApplication.java
│       ├── config/
│       │   ├── KeyResolverConfig.java           # Rate limit key resolvers (userId, IP)
│       │   └── RedisConfig.java                 # Redis + JWT cache manager
│       ├── controller/
│       │   └── FallbackController.java          # Circuit breaker fallback responses
│       ├── exception/
│       │   └── GatewayExceptionHandler.java     # WebExceptionHandler for reactive errors
│       ├── filters/
│       │   └── AuthenticationFilter.java        # Custom JWT gateway filter
│       └── service/
│           └── JwtService.java                  # Token validation + Redis caching
│
├── config-server/
│   └── src/main/java/com/dev/sphere/config_server/
│       └── ConfigServerApplication.java         # Spring Cloud Config Server
│
├── discovery-server/
│   └── src/main/java/com/dev/sphere/discovery_server/
│       └── DiscoveryServerApplication.java      # Eureka server entry point
│
├── user-service/
│   └── src/main/java/com/dev/sphere/userService/
│       ├── UserServiceApplication.java
│       ├── advice/
│       │   ├── ApiError.java
│       │   ├── ApiResponse.java
│       │   ├── GlobalExceptionHandler.java
│       │   └── GlobalResponseHandler.java
│       ├── auth/
│       │   ├── FeignClientInterceptor.java      # Propagates userId header via Feign
│       │   ├── UserContextHolder.java
│       │   ├── UserInterceptor.java
│       │   └── WebConfig.java
│       ├── clients/
│       │   ├── ConnectionsClient.java           # Feign → connection-service
│       │   ├── ConnectionsClientFallback.java
│       │   └── PostClient.java                  # Feign → post-service
│       ├── config/
│       │   ├── AppConfig.java                   # PasswordEncoder bean
│       │   ├── ModelMapperConfig.java
│       │   └── RedisConfig.java                 # Cache managers (profile, search, JWT)
│       ├── controller/
│       │   ├── AuthController.java
│       │   └── ProfileController.java
│       ├── dto/
│       ├── entity/
│       ├── exception/
│       ├── repository/
│       ├── service/
│       │   ├── AuthServiceImpl.java             # Registration, login, JWT issuance
│       │   ├── JwtService.java                  # JWT generation + Redis caching
│       │   └── ProfileServiceImpl.java          # Profile CRUD, search, caching
│       └── utils/
│           └── PasswordUtil.java
│
├── post-service/
│   └── src/main/java/com/dev/sphere/postService/
│       ├── PostServiceApplication.java
│       ├── clients/
│       │   ├── ConnectionsClient.java
│       │   └── ConnectionsClientFallback.java
│       ├── config/
│       │   ├── KafkaTopicConfig.java
│       │   └── RedisConfig.java                 # Redis template for like counts
│       ├── controller/
│       │   ├── LikesController.java
│       │   └── PostController.java
│       ├── entity/
│       │   ├── Post.java
│       │   └── PostLike.java
│       ├── event/
│       │   ├── PostCreatedEvent.java            # Kafka event — post created
│       │   └── PostLikedEvent.java              # Kafka event — post liked
│       ├── repository/
│       └── service/
│           ├── LikeServiceImpl.java             # Redis INCR/DECR + DB flush every 5
│           └── PostServiceImpl.java
│
├── connection-service/
│   └── src/main/java/com/dev/sphere/connection_service/
│       ├── auth/
│       │   └── UserContextHolder.java           # @Component for SpEL cache keys
│       ├── config/
│       │   ├── KafkaTopicConfig.java
│       │   └── RedisConfig.java
│       ├── controller/
│       │   └── ConnectionsController.java
│       ├── entity/
│       │   └── Person.java                      # Neo4j @Node entity
│       ├── event/
│       ├── repository/
│       │   └── PersonRepository.java            # Neo4j Cypher queries
│       └── service/
│           └── ConnectionsService.java          # Graph traversal, caching, Kafka events
│
└── notification-service/
    └── src/main/java/com/dev/sphere/notification_service/
        ├── clients/
        │   ├── ConnectionsClient.java
        │   ├── ConnectionsClientFallback.java
        ├── config/
        │   └── AppConfig.java
        ├── consumer/
        │   ├── ConnectionServiceConsumer.java   # Listens to connection events
        │   └── PostServiceConsumer.java         # Listens to post events + CB/retry
        ├── entity/
        │   └── Notification.java
        ├── repository/
            └── NorificationRepository.java
        └── service/
            └── SendNotification.java
```

### Service Responsibilities

| Service | Responsibility | Database |
|---|---|---|
| `api-gateway` | JWT validation, rate limiting, circuit breaker, routing | Redis |
| `config-server` | Centralized configuration for all services | Git / filesystem |
| `discovery-server` | Service registration and discovery (Eureka) | — |
| `user-service` | Registration, login, JWT, profiles, search, caching | PostgreSQL + Redis |
| `post-service` | Post CRUD, like counts (Redis INCR/DECR), Kafka events | PostgreSQL + Redis |
| `connection-service` | Follow graph, people you may know, connection caching | Neo4j + Redis |
| `notification-service` | Kafka consumer, Feign + circuit breaker, notifications | PostgreSQL |

---

## Key Features

**Authentication & Security**
- Custom JWT filter at the gateway — stateless token auth, no Spring Security
- JWT validation cached in Redis with TTL matching token expiry — crypto work done once per token lifetime
- All secrets managed via environment variables — no hardcoded credentials

**API Gateway**
- Single entry point — routes, strips path prefixes, enforces auth
- Rate limiting per userId using Redis token bucket — 429 on burst exceeded
- Circuit breaker per route — 503 + fallback response when service is down
- Proper WebFlux-native error handling via `WebExceptionHandler`

**Caching (Redis)**
- User profiles — 10 minute TTL, evicted on profile update
- Search results — wrapper pattern to handle `List<>` deserialization, 2 minute TTL
- JWT tokens — `RedisTemplate` with string serializer, TTL equals token expiry
- Like counts — atomic `INCR`/`DECR`, flushed to PostgreSQL every 5 likes
- Connection lists — `@Cacheable` with SpEL `UserContextHolder`, 5 minute TTL
- People you may know — 10 minute TTL on expensive 2-hop Neo4j traversal
- Stale cache protection — fallback responses are never cached

**Resilience (Resilience4j)**
- Circuit breaker on all Feign calls — opens after 50% failure rate in sliding window
- Retry with 3 attempts and 500ms backoff on transient failures
- Fallback responses for every failure path — no cascading failures
- Gateway-level circuit breaker as the outer protection layer

**Event-Driven Architecture (Kafka)**
- Post created → notifies all first-degree connections asynchronously
- Post liked → notifies post creator
- Connection request sent/accepted → notifies receiver/sender
- Producers never block waiting for notification-service

**Graph-Based Connections (Neo4j)**
- Follow/unfollow modeled as directed graph relationships
- First-degree connection queries via Cypher traversal
- "People you may know" — 2-hop graph traversal, cached in Redis
- Scales naturally — graph queries stay fast regardless of connection count

**Observability**
- Distributed tracing via Zipkin + Micrometer — trace spans across all services
- Actuator health endpoints on every service
- Structured logging with `@Slf4j` and contextual fields (userId, postId, etc.)

**Centralized Configuration**
- Spring Cloud Config Server — all service configs managed in one place
- `@RefreshScope` support — update configs without restarting services
- Environment-specific config files supported (dev, prod)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Service Discovery | Netflix Eureka |
| Inter-service Calls | OpenFeign + Resilience4j |
| Async Messaging | Apache Kafka |
| Caching | Redis (Lettuce) |
| Auth | Custom JWT filter (jjwt 0.12.6) |
| Databases | PostgreSQL · Neo4j |
| ORM | Spring Data JPA · Spring Data Neo4j |
| Resilience | Resilience4j (circuit breaker, retry, timelimiter) |
| Tracing | Micrometer + Zipkin |
| Config | Spring Cloud Config Server |
| Build | Maven |

---

## Challenges Faced & How I Solved Them

### 1. Setting Up Apache Kafka — Local and In-Code

**Challenge:** Configuring Zookeeper, Kafka broker, topics, serializers, and consumer groups from scratch — plus debugging `ClassNotFoundException` from package mismatches between producer and consumer services.

**How I solved it:** Read the official Kafka and Spring Kafka documentation extensively. Settled on consistent `application.yml`-based configuration with `spring.json.trusted.packages: "*"` and explicit value default type mappings. Eliminated manual `@Bean` configs in favour of Spring Boot auto-configuration.

**Scalability improvement:** Notification-service is fully decoupled from all producers. Adding a new event type requires only a new Kafka topic and `@KafkaListener` — zero changes to any producer service.

---

### 2. Redis Serialization for Cached Lists

**Challenge:** `@Cacheable` on methods returning `List<T>` caused Jackson deserialization failures — the top-level array had no `@class` type metadata, so Jackson couldn't reconstruct the typed list on cache read.

**How I solved it:** Wrapped list results in dedicated wrapper classes (`SearchResultsWrapper`, `ConnectionsWrapper`). Redis stores the wrapper with its `@class` metadata intact, enabling clean deserialization. Single objects continue to use `@Cacheable` directly.

**Scalability improvement:** Cache pattern is now consistent and safe for all return types — no silent cache misses or runtime deserialization errors.

---

### 3. @Cacheable Incompatibility with Spring Cloud Gateway (WebFlux)

**Challenge:** `@Cacheable` is a Spring MVC (servlet) abstraction — it doesn't work inside a WebFlux reactive pipeline. JWT caching annotations were silently ignored. Adding `spring-webmvc` to fix it caused unpredictable behaviour by mixing both stacks.

**How I solved it:** Removed `spring-webmvc` from the gateway entirely. Implemented JWT caching manually using `RedisTemplate` directly inside `JwtService.getIdFromTheToken()` — check Redis first, parse JWT on cache miss, store result with 10-minute TTL. Clean, explicit, and reactive-safe.

---

### 4. Inter-service Communication via Feign Client

**Challenge:** Mismatched response types, incorrect path mappings after `StripPrefix`, and Feign clients routing through the gateway instead of directly to services caused persistent errors.

**How I solved it:** Tested each target endpoint directly in Postman first, then aligned Feign interface signatures exactly with controller signatures. Feign clients use Eureka service names (`lb://SERVICE-NAME`) to bypass the gateway entirely for internal calls.

---

### 5. Cascading Failures Between Services

**Challenge:** When connection-service was slow or down, calling services piled up waiting for Feign responses — exhausting thread pools and causing cascading failures across the system.

**How I solved it:** Added Resilience4j circuit breakers and retry on every Feign client. After 5 consecutive failures, the circuit opens and fallback responses return immediately. Services recover automatically when the dependency comes back up.

---

### 6. Graph Modeling for Connections

**Challenge:** Modeling follow relationships and "people you may know" in PostgreSQL would require expensive multi-join queries that degrade at scale.

**How I solved it:** Used Neo4j — users are `@Node` entities, connections are `@Relationship` properties. "People you may know" is a single 2-hop Cypher traversal. Results cached in Redis for 10 minutes to avoid repeated graph queries.

---

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven
- Redis running on `localhost:6379`

### Environment Variables

```bash
export JWT_SECRET=your_strong_secret_key_here
export MAIL_USERNAME=youremail@gmail.com
export MAIL_PASSWORD=your_gmail_app_password
export FRONTEND_URL=http://localhost:3000
```

### Start Order

```bash
# 1. Infrastructure
docker-compose up -d  # PostgreSQL, Neo4j, Kafka, Redis, Zipkin

# 2. Discovery server
cd discovery-server && mvn spring-boot:run

# 3. Config server
cd config-server && mvn spring-boot:run

# 4. All services (any order after step 3)
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd post-service && mvn spring-boot:run
cd connection-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### Service Ports

| Service | Port |
|---|---|
| API Gateway | 8080 |
| Discovery Server (Eureka UI) | 8761 |
| Config Server | 8888 |
| User Service | 9020 |
| Post Service | 9010 |
| Connection Service | 9030 |
| Notification Service | 9040 |
| Zipkin UI | 9411 |

### Example API Calls

```bash
# Register
POST http://localhost:8080/api/v1/auth/signup

# Login
POST http://localhost:8080/api/v1/auth/login

# Create a post (authenticated)
POST http://localhost:8080/api/v1/posts/core
Authorization: Bearer <your_jwt_token>

# Get all my posts (authenticated)
GET http://localhost:8080/api/v1/posts/core/users/allMyPosts
Authorization: Bearer <your_jwt_token>

# Send connection request (authenticated)
POST http://localhost:8080/api/v1/connections/core/send/{receiverId}
Authorization: Bearer <your_jwt_token>

# Get first-degree connections (authenticated)
GET http://localhost:8080/api/v1/connections/core/first-degree
Authorization: Bearer <your_jwt_token>

# People you may know (authenticated)
GET http://localhost:8080/api/v1/connections/core/second-degree
Authorization: Bearer <your_jwt_token>
```

### Testing Rate Limiting

```bash
TOKEN="your_access_token_here"
for i in {1..25}; do curl -s -o /dev/null -w "Request $i: %{http_code}\n" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/posts/core/users/allMyPosts; done
```

Expected: first N requests return `200`, remaining return `429 Too Many Requests`.

### Testing Circuit Breaker

Stop `post-service` and hit any post endpoint — gateway returns:

```json
{
  "status": "SERVICE_UNAVAILABLE",
  "message": "Post service is currently unavailable. Please try again later.",
  "service": "post-service"
}
```

Restart `post-service` — traffic resumes automatically after 10 seconds.

### Monitoring Redis

```bash
redis-cli KEYS "sphere:*"                                    # All Sphere cache keys
redis-cli GET "request_rate_limiter.{user:1}.tokens"         # Token bucket count
redis-cli GET "sphere:post:likes:1"                          # Like count for post 1
redis-cli GET "sphere:user:jwt:eyJhbG..."                    # Cached JWT userId
```

### Distributed Tracing

Open Zipkin at `http://localhost:9411` — every authenticated request generates a trace spanning gateway → service → database.

---

## What's Coming Next

- [ ] Password reset flow
- [ ] Direct messaging service (WebSocket + Kafka)
- [ ] CI/CD pipeline (GitHub Actions)

---

## Author

**HEYDEV001**
[github.com/HEYDEV001](https://github.com/HEYDEV001)

---

> Built from scratch. Every service, every Kafka topic, every circuit breaker, every cache — designed, debugged, and shipped solo.
