FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

ENV TESTCONTAINERS_RYUK_DISABLED=true
ENV DOCKER_HOST=tcp://host.docker.internal:2375

RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]