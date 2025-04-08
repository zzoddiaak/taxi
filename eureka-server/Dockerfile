FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1. Копируем POM файлы
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml

# 2. Скачиваем зависимости
RUN mvn -f eureka-server/pom.xml dependency:go-offline

# 3. Копируем исходный код
COPY eureka-server/src eureka-server/src

# 4. Собираем
RUN mvn -f eureka-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]