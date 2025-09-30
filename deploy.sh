#!/bin/bash

# Navigate to the root directory of your Spring Boot project
# cd /path/to/your/project

# Build your Spring Boot application
mvn clean package

# Create S3 bucket (replace with your preferred bucket name)
aws s3 mb s3://deyvedev-deploy-backend-bucket --profile deyvedev

# Upload your JAR file
aws s3 cp target/grain-pay-api-*.jar s3://deyvedev-deploy-backend-bucket/grain-pay-api.jar --profile deyvedev

# Create key pair
aws ec2 create-key-pair --key-name springboot-keypair --query 'KeyMaterial' --output text > springboot-keypair.pem --profile deyvedev

# Set permissions
chmod 400 springboot-keypair.pem

# Deploy DEV stack
aws cloudformation create-stack \
  --stack-name springboot-dev \
  --template-body file://aws/deploy-dev.yaml \
  --parameters \
    ParameterKey=KeyName,ParameterValue=springboot-keypair \
    ParameterKey=SpringBootJarS3Bucket,ParameterValue=deyvedev-deploy-backend-bucket \
    ParameterKey=SpringBootJarS3Key,ParameterValue=grain-pay-api.jar \
  --capabilities CAPABILITY_IAM \
  --profile deyvedev

# Update the stack if it already exists
aws cloudformation update-stack \
  --stack-name springboot-dev \
  --template-body file://aws/deploy-dev.yaml \
  --parameters \
    ParameterKey=KeyName,ParameterValue=springboot-keypair \
    ParameterKey=SpringBootJarS3Bucket,ParameterValue=deyvedev-deploy-backend-bucket \
    ParameterKey=SpringBootJarS3Key,ParameterValue=grain-pay-api.jar \
  --capabilities CAPABILITY_IAM \
  --profile deyvedev

# Delete the stack if needed (uncomment to use)
aws cloudformation delete-stack \
  --stack-name springboot-dev \
  --profile deyvedev

# Get the latest Amazon Linux 2 AMI ID with kernel 5.10
aws ec2 describe-images \
  --owners amazon \
  --filters "Name=name,Values=amzn2-ami-kernel-5.10-hvm-*-x86_64-gp2" \
            "Name=state,Values=available" \
  --query 'Images[*].[ImageId,CreationDate]' \
  --region us-east-1 \
  --output text \
  --profile deyvedev | sort -k2 -r | head -n1

# Monitor deployment
aws cloudformation describe-stacks --stack-name springboot-dev --query 'Stacks[0].StackStatus' --profile deyvedev
