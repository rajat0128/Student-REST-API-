# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile for Student API (Spring Boot)
#
# CONCEPT: Multi-stage build
#   Stage 1 (builder) - Compiles and packages the app using Maven
#   Stage 2 (runtime) - Creates a lean image with only the JAR file
#
# This keeps the final image small (~200MB vs ~600MB with full JDK+Maven)
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer separately.
# Dependencies are only re-downloaded when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the application (skip tests here; Jenkins runs them separately)
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
# Use a smaller base image (JRE only, no compiler)
FROM eclipse-temurin:17-jre-alpine

# Good practice: don't run as root inside containers
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copy only the JAR from the builder stage
COPY --from=builder /app/target/student-api-1.0.0.jar app.jar

# Document that the container listens on port 8080
EXPOSE 8080

# Health check: Docker will poll this every 30s
# If /actuator/health returns non-200, container is marked 'unhealthy'
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
