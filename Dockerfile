FROM openjdk:21

COPY build/libs/writer.jar /writer.jar

ENTRYPOINT ["java", "-jar", "/writer.jar"]
