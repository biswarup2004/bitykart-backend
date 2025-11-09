# Step 1: Build stage using Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and pre-download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the project files and build the jar
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run stage using lightweight Java 21 image
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/bitykart-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render assigns dynamically but we’ll still expose 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
