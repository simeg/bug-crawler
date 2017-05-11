FROM frolvlad/alpine-oraclejdk8:slim
# Where Spring Boot application creates working dirs for Tomcat by default
VOLUME /tmp
# Where the project JAR is added
ADD bug-crawler-container-0.1.0.jar app.jar
# Touch it so we can have modification time
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dapp.port=${app.port}", "-jar","/app.jar"]
