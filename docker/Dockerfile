FROM adoptopenjdk:11-jre-hotspot
MAINTAINER resource_service
COPY resource_service-1.0-SNAPSHOT.jar resource_service.jar
ENTRYPOINT ["java","-jar","/resource_service.jar"]
COPY Song_Service-1.0-SNAPSHOT.jar song_service.jar
ENTRYPOINT ["java","-jar","/song_service.jar"]
COPY resource_processor-1.0-SNAPSHOT.jar resource_processor.jar
ENTRYPOINT ["java","-jar","/resource_processor.jar"]
