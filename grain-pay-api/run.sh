#!/bin/bash

# Navigate to the root directory of your Spring Boot project
# cd /path/to/your/project

# Build the project using Maven or Gradle
./mvnw clean package   # For Maven

# Run the Spring Boot application
java -jar target/grain-pay-api-0.0.1-SNAPSHOT.jar
