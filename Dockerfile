FROM openjdk:17-jdk-slim

RUN mkdir -p /var/log

COPY /target/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "app.jar"]