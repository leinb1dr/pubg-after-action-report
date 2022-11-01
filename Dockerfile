FROM openjdk:14-alpine
EXPOSE 8080
ADD /build/libs/after-action-report-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]