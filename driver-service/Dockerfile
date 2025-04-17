FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY driver-service/pom.xml driver-service/pom.xml

RUN mvn -f driver-service/pom.xml dependency:go-offline

COPY driver-service/src driver-service/src

RUN mvn -f driver-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/driver-service/target/driver-service-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]