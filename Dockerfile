# Use an official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# Copy the Spring Boot app's jar file to the container
COPY target/mamlaka-0.0.1-SNAPSHOT.jar mamlaka-0.0.1-SNAPSHOT.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","mamlaka-0.0.1-SNAPSHOT.jar"]
