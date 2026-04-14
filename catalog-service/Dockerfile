FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar catalogApp.jar
ENTRYPOINT ["java", "-jar", "catalogApp.jar"]