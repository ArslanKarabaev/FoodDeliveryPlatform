FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar orderApp.jar
ENTRYPOINT ["java", "-jar", "orderApp.jar"]