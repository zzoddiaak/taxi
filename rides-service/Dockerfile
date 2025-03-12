FROM eclipse-temurin:17 AS runtime

WORKDIR /app

COPY target/rides-service-0.0.1-SNAPSHOT.jar rides-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "rides-service.jar"]
