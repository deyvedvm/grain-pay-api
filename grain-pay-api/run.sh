#!/bin/bash

# Navigate to the root directory of your Spring Boot project
# cd /path/to/your/project

# Stop and remove the running containers
docker rm grain-pay-api
docker rm grain-pay-postgres

# Build the project using Maven or Gradle
./mvnw clean install

# Build the Docker image
docker-compose build

# Run the Docker container
docker-compose up

# Run the Spring Boot application
# java -jar target/grain-pay-api-0.0.1-SNAPSHOT.jar
