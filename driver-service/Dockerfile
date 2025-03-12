FROM eclipse-temurin:17 AS runtime

WORKDIR /app

COPY target/driver-service-0.0.1-SNAPSHOT.jar driver-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "driver-service.jar"]
