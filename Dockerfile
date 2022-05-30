FROM openjdk:11
EXPOSE 9000
COPY build/libs/group.jar /opt/group.jar
RUN apt-get update && apt-get upgrade -y && apt-get install curl telnet vim -y && chmod 755 /opt/group.jar
ENTRYPOINT ["java","-jar","/opt/group.jar"]