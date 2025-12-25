FROM eclipse-temurin:21-jre
EXPOSE 8080
ADD build/libs/badminton-service*.jar app.jar
CMD ["java", "-jar", "/app.jar"]