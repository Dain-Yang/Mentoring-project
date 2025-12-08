
FROM gradle:8.5-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test --no-daemon

FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]

