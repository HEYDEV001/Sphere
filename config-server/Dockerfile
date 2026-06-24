# Stage 1 — Build
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# copy pom first — Docker caches this layer
# dependencies only re-download when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2 — Run
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN useradd -m sphere
USER sphere

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]