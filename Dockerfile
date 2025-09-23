FROM openjdk:21-jdk-slim

WORKDIR /app

# 👇 Install curl so ECS health checks workS
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the Spring Boot JAR from Maven builds
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
