# Sphere

> A LinkedIn-like professional networking platform built with Spring Boot microservices — featuring Redis caching, rate limiting, circuit breakers, distributed tracing, centralized configuration, and full Docker Compose deployment.

---

## Project Objective

Sphere is a backend-focused professional networking platform designed to replicate the core functionality of LinkedIn. Built as a production-grade distributed system using modern microservices architecture — covering real-world concerns like asynchronous event-driven communication, inter-service resilience, graph-based relationship modeling, multi-layer caching, secure JWT authentication, password reset via email, and full containerized deployment — all managed through a unified API Gateway and centralized configuration server.

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
  ├──▶ User Service          → Auth, profiles, registration, search, password reset
  ├──▶ Post Service          → Post CRUD, likes (Redis INCR/DECR)
  ├──▶ Connection Service    → Follow graph (Neo4j), people you may know
  └──▶ Notification Service  → Kafka consumer, event-driven alerts, SendGrid email

  All services register with Eureka Discovery Server
  Services communicate via Feign Client (sync) and Kafka (async)
  Redis shared across all services for caching
  Zipkin + Micrometer for distributed tracing
  Full deployment via Docker Compose
```

---

## Project Structure

```
Sphere/
│
├── docker-compose.yml                           # Full system deployment
├── .env                                         # Environment variables (not committed)
│
├── api-gateway/
│   ├── Dockerfile                               # Multi-stage build (distroless)
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
│   ├── Dockerfile
│   └── src/main/java/com/dev/sphere/config_server/
│       └── ConfigServerApplication.java         # Spring Cloud Config Server
│
├── discovery-server/
│   ├── Dockerfile
│   └── src/main/java/com/dev/sphere/discovery_server/
│       └── DiscoveryServerApplication.java      # Eureka server entry point
│
├── user-service/
│   ├── Dockerfile
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
│       │   ├── AuthController.java              # signup, login, refresh, forgot/reset password
│       │   └── ProfileController.java
│       ├── dto/
│       │   ├── ForgotPasswordRequestDto.java
│       │   └── ResetPasswordRequestDto.java
│       ├── entity/
│       ├── events/
│       │   └── PasswordResetEvent.java          # Kafka event for password reset
│       ├── exception/
│       ├── repository/
│       ├── service/
│       │   ├── AuthServiceImpl.java             # Registration, login, JWT issuance
│       │   ├── JwtService.java                  # JWT generation + Redis caching
│       │   ├── PasswordResetService.java        # Forgot/reset password with Redis tokens
│       │   └── ProfileServiceImpl.java          # Profile CRUD, search, caching
│       └── utils/
│           └── PasswordUtil.java
│
├── post-service/
│   ├── Dockerfile
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
│       │   ├── PostCreatedEvent.java
│       │   └── PostLikedEvent.java
│       ├── repository/
│       └── service/
│           ├── LikeServiceImpl.java             # Redis INCR/DECR + DB flush every 5
│           └── PostServiceImpl.java
│
├── connection-service/
│   ├── Dockerfile
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
│           └── ConnectionsService.java
│
└── notification-service/
    ├── Dockerfile
    └── src/main/java/com/dev/sphere/notification_service/
        ├── clients/
        │   ├── ConnectionsClient.java
        │   └── ConnectionsClientFallback.java
        ├── config/
        │   └── AppConfig.java
        ├── consumer/
        │   ├── ConnectionServiceConsumer.java   # Listens to connection events
        │   ├── PasswordResetConsumer.java       # Listens to password reset events → SendGrid
        │   └── PostServiceConsumer.java         # Listens to post events + CB/retry
        ├── entity/
        │   └── Notification.java
        ├── repository/
        │   └── NotificationRepository.java
        └── service/
            ├── EmailService.java                # SendGrid email sender
            └── SendNotification.java
```

### Service Responsibilities

| Service | Responsibility | Database |
|---|---|---|
| `api-gateway` | JWT validation, rate limiting, circuit breaker, routing | Redis |
| `config-server` | Centralized configuration for all services | GitHub repo |
| `discovery-server` | Service registration and discovery (Eureka) | — |
| `user-service` | Registration, login, JWT, profiles, search, password reset | PostgreSQL + Redis |
| `post-service` | Post CRUD, like counts (Redis INCR/DECR), Kafka events | PostgreSQL + Redis |
| `connection-service` | Follow graph, people you may know, connection caching | Neo4j + Redis |
| `notification-service` | Kafka consumer, circuit breaker, notifications, SendGrid email | PostgreSQL |

---

## Key Features

**Authentication & Security**
- Custom JWT filter at the gateway — stateless token auth, no Spring Security
- JWT validation cached in Redis with TTL matching token expiry — crypto work done once per token lifetime
- All secrets managed via environment variables — no hardcoded credentials

**Password Reset Flow**
- User requests reset → user-service generates UUID token, stores in Redis with 15-minute TTL
- `PasswordResetEvent` published to Kafka — user-service never sends email directly
- notification-service consumes event → EmailService sends HTML reset email via SendGrid
- User submits token + new password → user-service validates Redis token, updates DB, deletes token
- Token is single-use and auto-expires — no cleanup job needed

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
- Password reset tokens — UUID stored with 15-minute TTL, deleted on use

**Resilience (Resilience4j)**
- Circuit breaker on all Feign calls — opens after 50% failure rate in sliding window
- Retry with 3 attempts and 500ms backoff on transient failures
- Fallback responses for every failure path — no cascading failures
- Gateway-level circuit breaker as the outer protection layer
- Feign fallback classes on every client — signup succeeds even when connection-service is down

**Event-Driven Architecture (Kafka)**
- Post created → notifies all first-degree connections asynchronously
- Post liked → notifies post creator
- Connection request sent/accepted → notifies receiver/sender
- Password reset requested → notification-service sends email via SendGrid
- Producers never block waiting for notification-service
- KRaft mode Kafka — no Zookeeper dependency

**Graph-Based Connections (Neo4j)**
- Follow/unfollow modeled as directed graph relationships
- First-degree connection queries via Cypher traversal
- "People you may know" — 2-hop graph traversal, cached in Redis
- Scales naturally — graph queries stay fast regardless of connection count

**Observability**
- Distributed tracing via Zipkin + Micrometer — trace spans across all services
- Actuator health endpoints on every service
- Structured logging with `@Slf4j` and contextual fields (userId, postId, etc.)
- Kafbat UI for Kafka topic and message monitoring

**Centralized Configuration**
- Spring Cloud Config Server — all service configs fetched from GitHub repo at startup
- `@RefreshScope` support — update configs without restarting services
- Environment-specific config files supported (dev, prod)
- All container hostnames used in config — no localhost references

**Docker Compose Deployment**
- Single `docker-compose up -d` starts the entire system
- Multi-stage Dockerfiles — distroless base image, non-root user, ~150MB final image size
- Health checks on all services — proper startup ordering guaranteed
- Separate PostgreSQL instance per service — no shared database
- Named volumes for all persistent data — Redis, PostgreSQL, Neo4j, Kafka
- All secrets injected via `.env` file — nothing hardcoded

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Service Discovery | Netflix Eureka |
| Inter-service Calls | OpenFeign + Resilience4j |
| Async Messaging | Apache Kafka (KRaft mode) |
| Caching | Redis (Lettuce) |
| Auth | Custom JWT filter (jjwt 0.12.6) |
| Databases | PostgreSQL · Neo4j |
| ORM | Spring Data JPA · Spring Data Neo4j |
| Resilience | Resilience4j (circuit breaker, retry, timelimiter) |
| Email | SendGrid API |
| Tracing | Micrometer + Zipkin |
| Config | Spring Cloud Config Server + GitHub |
| Containerization | Docker + Docker Compose |
| Build | Maven |

---

## Challenges Faced & How I Solved Them

### 1. Setting Up Apache Kafka — Local and In-Code

**Challenge:** Configuring Zookeeper, Kafka broker, topics, serializers, and consumer groups from scratch — plus debugging `ClassNotFoundException` from package mismatches between producer and consumer services.

**How I solved it:** Settled on consistent `application.yml`-based configuration with `spring.json.trusted.packages: "*"` and explicit value default type mappings. Switched to KRaft mode Kafka — eliminates Zookeeper entirely, simpler setup and fewer moving parts in Docker.

**Scalability improvement:** Notification-service is fully decoupled from all producers. Adding a new event type requires only a new Kafka topic and `@KafkaListener` — zero changes to any producer service.

---

### 2. Redis Serialization for Cached Lists

**Challenge:** `@Cacheable` on methods returning `List<T>` caused Jackson deserialization failures — the top-level array had no `@class` type metadata, so Jackson couldn't reconstruct the typed list on cache read.

**How I solved it:** Wrapped list results in dedicated wrapper classes (`SearchResultsWrapper`, `ConnectionsWrapper`). Redis stores the wrapper with its `@class` metadata intact, enabling clean deserialization.

---

### 3. @Cacheable Incompatibility with Spring Cloud Gateway (WebFlux)

**Challenge:** `@Cacheable` is a Spring MVC abstraction — it doesn't work inside a WebFlux reactive pipeline. Adding `spring-webmvc` to fix it caused unpredictable behaviour by mixing both stacks.

**How I solved it:** Removed `spring-webmvc` entirely. Implemented JWT caching manually using `RedisTemplate` directly inside `JwtService.getIdFromTheToken()`. Clean, explicit, and reactive-safe.

---

### 4. Inter-service Communication via Feign Client

**Challenge:** Mismatched response types, incorrect path mappings after `StripPrefix`, and Feign clients accidentally routing through the gateway instead of directly to services.

**How I solved it:** Feign clients use Eureka service names (`lb://SERVICE-NAME`) to bypass the gateway entirely for internal calls. Every Feign client has a fallback class — callers degrade gracefully when dependencies are down.

---

### 5. Cascading Failures Between Services

**Challenge:** When connection-service was slow or down, calling services piled up waiting for Feign responses — exhausting thread pools and causing cascading failures.

**How I solved it:** Added Resilience4j circuit breakers and retry on every Feign client. After 5 consecutive failures the circuit opens, fallback responses return immediately, and services recover automatically when the dependency comes back.

---

### 6. Spring AOP Self-Invocation Breaking Circuit Breakers

**Challenge:** `@CircuitBreaker` annotations were silently ignored when a method called another annotated method within the same class — Spring AOP proxies don't intercept self-calls, so there was no fallback and the error `No fallback available` was thrown.

**How I solved it:** Moved Feign client calls into a dedicated `ConnectionsClientService` bean. Spring can now proxy the call correctly, circuit breaker triggers as expected, and fallbacks work.

---

### 7. Graph Modeling for Connections

**Challenge:** Modeling follow relationships and "people you may know" in PostgreSQL would require expensive multi-join queries that degrade at scale.

**How I solved it:** Used Neo4j — users are `@Node` entities, connections are `@Relationship` properties. "People you may know" is a single 2-hop Cypher traversal cached in Redis for 10 minutes.

---

### 8. Dockerizing a Microservices System

**Challenge:** Services using `localhost` to reach each other work fine locally but fail completely in Docker — each container has its own network namespace and `localhost` refers to itself.

**How I solved it:** Moved all service configs to the centralized config repo and replaced every `localhost` reference with the Docker container name. Health checks on all services ensure correct startup order — no service starts before its dependencies are actually ready, not just running.

---

## Getting Started

### Prerequisites

- Docker and Docker Compose
- A SendGrid account with a verified sender email
- A GitHub repo for centralized config (already set up at `github.com/HEYDEV001/Sphere-Config-server`)

### Environment Variables

Create a `.env` file at the project root:

```bash
DB_PASS=your_db_password
REDIS_PASSWORD=your_redis_password
JWT_SECRET_KEY=your_jwt_secret_key
SENDGRID_API_KEY=your_sendgrid_api_key
SENDGRID_FROM_EMAIL=your_verified_sender@email.com
FRONTEND_URL=http://localhost:3000
GIT_USERNAME=your_github_username
GIT_TOKEN=your_github_personal_access_token
```

Never commit `.env` — it is in `.gitignore`.

### Start the entire system

```bash
docker compose up -d
```

Check all services are healthy:

```bash
docker compose ps
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
| Kafbat UI | 8090 |

### Example API Calls

```bash
# Register
POST http://localhost:8080/api/v1/auth/signup

# Login
POST http://localhost:8080/api/v1/auth/login

# Forgot password
POST http://localhost:8080/api/v1/auth/forgot-password
{"email": "user@example.com"}

# Reset password
POST http://localhost:8080/api/v1/auth/reset-password
{"token": "uuid-from-email", "newPassword": "newPass123", "confirmPassword": "newPass123"}

# Create a post (authenticated)
POST http://localhost:8080/api/v1/posts/core
Authorization: Bearer <your_jwt_token>

# Like a post (authenticated)
POST http://localhost:8080/api/v1/posts/likes/{postId}
Authorization: Bearer <your_jwt_token>

# Send connection request (authenticated)
POST http://localhost:8080/api/v1/connections/core/request/{receiverId}
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

Stop `post-service` and hit any post endpoint:

```bash
docker compose stop post-service
curl http://localhost:8080/api/v1/posts/core/users/allMyPosts \
  -H "Authorization: Bearer $TOKEN"
```

Expected fallback response:

```json
{
  "status": "SERVICE_UNAVAILABLE",
  "message": "Post service is currently unavailable. Please try again later.",
  "service": "post-service"
}
```

Restart post-service — traffic resumes automatically after 10 seconds:

```bash
docker compose start post-service
```

### Monitoring Redis

```bash
redis-cli -a $REDIS_PASSWORD KEYS "sphere:*"
redis-cli -a $REDIS_PASSWORD GET "sphere:post:likes:1"
redis-cli -a $REDIS_PASSWORD KEYS "sphere:password:reset:*"
redis-cli -a $REDIS_PASSWORD GET "request_rate_limiter.{user:1}.tokens"
```

### Distributed Tracing

Open Zipkin at `http://localhost:9411` — every authenticated request generates a trace spanning gateway → service → database.

### Kafka Monitoring

Open Kafbat UI at `http://localhost:8090` — monitor topics, consumer groups, message throughput, and lag in real time.

---

## What's Coming Next

- [ ] Direct messaging service (WebSocket + Kafka)
- [ ] CI/CD pipeline (GitHub Actions)

---

## Author

**HEYDEV001**
[github.com/HEYDEV001](https://github.com/HEYDEV001)

---

> Built from scratch. Every service, every Kafka topic, every circuit breaker, every cache, every Docker container — designed, debugged, and shipped solo.
