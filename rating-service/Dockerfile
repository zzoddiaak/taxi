FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY rating-service/pom.xml rating-service/pom.xml
RUN mvn -f rating-service/pom.xml dependency:go-offline

COPY rating-service/src rating-service/src
RUN mvn -f rating-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/rating-service/target/rating-service-*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]