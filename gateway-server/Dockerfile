FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY gateway-server/pom.xml gateway-server/pom.xml
RUN mvn -f gateway-server/pom.xml dependency:go-offline

COPY gateway-server/src gateway-server/src
RUN mvn -f gateway-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/gateway-server/target/gateway-server-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]