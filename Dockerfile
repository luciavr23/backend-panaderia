# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml . 
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Etapa final
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/PanaderiaAna-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]