FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY passenger-service/pom.xml passenger-service/pom.xml
RUN mvn -f passenger-service/pom.xml dependency:go-offline

COPY passenger-service/src passenger-service/src
RUN mvn -f passenger-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/passenger-service/target/passenger-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]