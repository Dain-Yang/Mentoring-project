
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]