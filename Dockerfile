FROM openjdk:11
EXPOSE 9000
COPY build/libs/organization.jar /opt/organization.jar
RUN chmod 755 /opt/organization.jar
ENTRYPOINT ["java","-jar","/opt/organization.jar"]