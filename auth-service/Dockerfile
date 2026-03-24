FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar authApp.jar
ENTRYPOINT ["java", "-jar", "authApp.jar"]