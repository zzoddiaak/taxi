FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY rides-service/pom.xml rides-service/pom.xml
RUN mvn -f rides-service/pom.xml dependency:go-offline

COPY rides-service/src rides-service/src
RUN mvn -f rides-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/rides-service/target/rides-service-*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]