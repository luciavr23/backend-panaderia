que nombre le pongo a este archivo en la raiz del proyecto?: # Etapa de construcción
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
ENTRYPOINT ["java", "-jar", "app.jar"]