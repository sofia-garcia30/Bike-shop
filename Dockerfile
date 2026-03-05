# Etapa 1: compilación con Maven
FROM maven:3.5.11-openjdk-21-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Descarga dependencias
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen final
FROM openjdk:21-jdk-slim
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]