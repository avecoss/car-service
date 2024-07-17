FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package

FROM openjdk:21-ea-31-slim

WORKDIR /app

EXPOSE 8080

COPY --from=build target/car-service*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]