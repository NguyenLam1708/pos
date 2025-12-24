# Stage 1: build Quarkus
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: chạy ứng dụng
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/target/quarkus-app/ /app/
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]
