FROM openjdk:17-jdk-alpine
COPY target/activity-manager-0.0.1-SNAPSHOT.jar activity-manager-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/activity-manager-0.0.1-SNAPSHOT.jar"]