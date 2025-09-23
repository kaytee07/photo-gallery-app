# ========================
# Stage 1: Build
# ========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven/Gradle wrapper and project files
COPY pom.xml mvnw* ./
COPY .mvn .mvn
COPY src src

# Build the Spring Boot app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# ========================
# Stage 2: Run
# ========================
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# 👇 Install curl for ECS health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]

