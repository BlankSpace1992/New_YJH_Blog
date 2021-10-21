FROM openjdk:8-jdk-alpine
VOLUME /tmp
MAINTAINER yujunhong
LABEL  by=yjh name=Blog-Web description="Blog-Web" version="1.0"
ADD target/Blog-Web-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE  8088
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "-Xms128M","-Xmx128M", "-Xmn64M", "-Xss256K","app.jar"]
