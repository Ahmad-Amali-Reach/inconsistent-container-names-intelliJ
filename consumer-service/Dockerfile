FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY src ./src

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build -x test

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "build/libs/consumer-service-1.0.0.jar"]