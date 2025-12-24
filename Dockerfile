FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/quarkus-app/ ./quarkus-app
EXPOSE 8080
CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]
