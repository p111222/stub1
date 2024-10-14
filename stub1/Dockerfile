# Use the Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and download dependencies (this step is cached if no changes in pom.xml)
COPY pom.xml .

# Download project dependencies
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Build the Spring Boot app using Maven
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight JDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Install curl
RUN apt-get update && apt-get install -y curl

# Copy the packaged jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Download and install the OpenTelemetry Java Agent
# Ensure you are using the correct version of the agent
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar

# Add environment variables for OpenTelemetry
ENV OTEL_TRACES_EXPORTER=jaeger \
    OTEL_SERVICE_NAME=stub1 \
    OTEL_EXPORTER_JAEGER_ENDPOINT=http://3.108.54.64:8347/api/traces \
    OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf \
    OTEL_RESOURCE_ATTRIBUTES=service.version=1.0,deployment.environment=prod

# Expose the port that your Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot app with the OpenTelemetry Java Agent
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]
