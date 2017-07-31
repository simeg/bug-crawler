FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Simon Egersand <s.egersand@gmail.com>

RUN apk update && apk add \
    postgresql

EXPOSE 8080
ENV JAVA_OPTS=""

# Where Spring Boot application creates working dirs for Tomcat by default
VOLUME /tmp

ENTRYPOINT ["/usr/bin/entrypoint.sh"]

COPY docker/crawler/entrypoint.sh /usr/bin/entrypoint.sh
COPY target/lib /usr/share/web-crawler/lib
COPY target/web-crawler-1.0.0.jar /usr/share/web-crawler/app.jar

RUN sh -c 'touch /usr/share/web-crawler/app.jar'
RUN chmod +x /usr/bin/entrypoint.sh

# Saving for reference. Can't find how to set app port in the entrypoint script.
# Not sure app port needs to be set, seems to be working any way.
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dapp.port=${app.port}", "-jar","/app.jar"]

