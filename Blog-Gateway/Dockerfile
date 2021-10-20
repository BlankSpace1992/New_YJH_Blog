FROM openjdk:8-jdk-alpine
VOLUME /tmp
MAINTAINER yujunhong
LABEL  by=yjh name=Blog-Gateway description="Blog-Gateway" version="1.0"
ADD target/Blog-Gateway-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE  8089
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "app.jar"]
