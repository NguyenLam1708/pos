# Stage 1: build Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src ./src
RUN ls -l /workspace/app
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/target/quarkus-app ./quarkus-app
EXPOSE 8080
CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]

