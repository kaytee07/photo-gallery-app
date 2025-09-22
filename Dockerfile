FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the Spring Boot JAR from Maven builds
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
