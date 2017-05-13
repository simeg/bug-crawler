FROM frolvlad/alpine-oraclejdk8:slim

RUN apk update && apk add \
    postgresql

EXPOSE 8080
ENV JAVA_OPTS=""

# Where Spring Boot application creates working dirs for Tomcat by default
VOLUME /tmp

ADD web-crawler-0.1.0.jar app.jar
RUN sh -c 'touch /app.jar'

COPY /entrypoint.sh /
RUN chmod +x /entrypoint.sh

# Saving for reference. Can't find how to set app port in the entrypoint script.
# Not sure app port needs to be set, seems to be working any way.
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dapp.port=${app.port}", "-jar","/app.jar"]

ENTRYPOINT ["/entrypoint.sh"]
