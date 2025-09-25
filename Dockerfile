# ========================
# Stage 1: Build
# ========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven files first for better caching (deps change less often)
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# Download deps (cached if pom unchanged)
RUN ./mvnw dependency:go-offline -B

# Copy source (rebuilds JAR with static files like index.html)
COPY src src

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests -B

# ========================
# Stage 2: Run
# ========================
FROM eclipse-temurin:21-jre-jammy  # JRE for smaller runtime image (~200MB vs 500MB)

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user for security
RUN useradd --create-home appuser
USER appuser

# Expose port
EXPOSE 8080

# Health check (optional; ECS can override)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
