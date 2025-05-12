FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml

RUN mvn -f eureka-server/pom.xml dependency:go-offline

COPY eureka-server/src eureka-server/src

RUN mvn -f eureka-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]