# Multi-stage build
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:8-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java", "-jar", "app.jar"]