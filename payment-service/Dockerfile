FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar paymentApp.jar
ENTRYPOINT ["java", "-jar", "paymentApp.jar"]