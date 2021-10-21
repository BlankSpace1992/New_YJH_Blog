FROM openjdk:8-jdk-alpine
VOLUME /tmp
MAINTAINER yujunhong
LABEL  by=yjh name=Blog-Admin description="Blog-Admin" version="1.0"
ADD target/Blog-Admin-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE  8087
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "-Xms128M","-Xmx128M", "-Xmn64M", "-Xss256K","app.jar"]
