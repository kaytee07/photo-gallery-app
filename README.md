# Photo Gallery App

## Overview
A Java Spring Boot app for a photo gallery, running in a Docker container on ECS Fargate. Uploads images to a private S3 bucket with 3-day presigned URLs and stores metadata in RDS PostgreSQL.

## Setup
- Install Java 21, Maven, Docker, AWS CLI.
- Set environment variables (for local testing):
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `AWS_S3_BUCKET`
  - `AWS_REGION`

## Build
```bash
mvn clean package
```

## Docker Build
```bash
docker build -t photo-gallery .
```

## Deploy

- Deploy infrastructure (photo-gallery-infra repo).
- Run build-deploy.sh to build, push to ECR, and upload appspec.yaml/taskdef.json to S3.
- CodePipeline triggers deployment on ECR push.
- Access the app at the ALB DNS (alb-LoadBalancerDnsName).

## Local Run
```bash
mvn spring-boot:run
```

Access: http://localhost:8080/```
