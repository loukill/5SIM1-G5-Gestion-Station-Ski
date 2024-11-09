FROM openjdk:17
EXPOSE 8089
COPY target/gestion-station-ski-0.0.1-SNAPSHOT.jar gestion-station-ski-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/gestion-station-ski-0.0.1-SNAPSHOT.jar"]