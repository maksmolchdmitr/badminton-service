FROM --platform=linux/amd64 eclipse-temurin:21-jre
EXPOSE 8080
ADD build/libs/badminton-service*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]