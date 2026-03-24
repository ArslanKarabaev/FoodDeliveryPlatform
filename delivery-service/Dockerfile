FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar deliveryApp.jar
ENTRYPOINT ["java", "-jar", "deliveryApp.jar"]