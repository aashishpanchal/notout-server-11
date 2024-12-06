# Use an official Maven image to build the app
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for faster build in production)
RUN mvn clean package -DskipTests

# Use a Tomcat base image to run the WAR file
FROM tomcat:9.0-jdk17-openjdk-slim

# Set the working directory for Tomcat
WORKDIR /usr/local/tomcat

# Copy the WAR file from the build stage to the Tomcat webapps folder
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080 for the application
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]
