FROM eclipse-temurin:17 AS runtime

WORKDIR /app

COPY target/rating-service-0.0.1-SNAPSHOT.jar rating-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "rating-service.jar"]
