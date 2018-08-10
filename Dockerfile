FROM java:8-alpine

ADD target/hive-0.0.1-SNAPSHOT-standalone.jar /hive/app.jar

EXPOSE 8080 2222

CMD ["java", "-jar", "/hive/app.jar"]
