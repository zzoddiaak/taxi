FROM eclipse-temurin:17 AS runtime

WORKDIR /app

COPY target/passenger-service-0.0.1-SNAPSHOT.jar passenger-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "passenger-service.jar"]
