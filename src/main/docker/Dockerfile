# Where image is downladed from
FROM frolvlad/alpine-oraclejdk8:slim
# Where Spring Boot application creates working dirs for Tomcat by default
VOLUME /tmp
# Where the project JAR is added
ADD gs-spring-boot-docker-0.1.0.jar app.jar
# Touch it so we can have modification time
RUN sh -c 'touch /app.jar'
# To reduce [Tomcat startup time)[https://wiki.apache.org/tomcat/HowTo/FasterStartUp#Entropy_Source]
#  we added a system property pointing to "/dev/urandom" as a source of entropy.
ENV JAVA_OPTS=""
# Project JAR is executed here
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
