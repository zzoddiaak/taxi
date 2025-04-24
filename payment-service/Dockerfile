FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY payment-service/pom.xml payment-service/pom.xml
RUN mvn -f payment-service/pom.xml dependency:go-offline

COPY payment-service/src payment-service/src
RUN mvn -f payment-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/payment-service/target/payment-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]