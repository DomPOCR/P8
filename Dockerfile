FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=build/TourGuide_jar/TourGuide-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} TourGuide.jar
ENTRYPOINT ["java","-jar","/TourGuide.jar"]