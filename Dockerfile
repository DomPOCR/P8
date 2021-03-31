FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} TourGuide-1.0.0.jar
ENTRYPOINT ["java","-jar","/TourGuide-1.0.0.jar"]