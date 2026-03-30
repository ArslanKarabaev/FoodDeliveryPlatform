FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar notificationApp.jar
ENTRYPOINT ["java", "-jar", "notificationApp.jar"]