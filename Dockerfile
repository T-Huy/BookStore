FROM openjdk:17

ARG File_JAR=target/*.jar

ADD ${File_JAR} api-service.jar

ENTRYPOINT ["java", "-jar", "api-service.jar"]

EXPOSE 8080