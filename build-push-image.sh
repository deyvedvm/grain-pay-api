#!/bin/bash
set -e

# Build Spring Boot JAR
mvn clean package -DskipTests

# Authenticate Docker to ECR
aws ecr get-login-password --region us-east-1 --profile deyvedvm \
  | docker login --username AWS --password-stdin 452503617370.dkr.ecr.us-east-1.amazonaws.com

# Build Docker image
docker build -t springboot-app .

# Tag image
docker tag springboot-app:latest 452503617370.dkr.ecr.us-east-1.amazonaws.com/springboot-app:latest

# Push to ECR
docker push 452503617370.dkr.ecr.us-east-1.amazonaws.com/springboot-app:latest
