FROM openjdk:8-jdk-alpine
VOLUME /tmp
MAINTAINER yujunhong
LABEL  by=yjh name=Blog-RabbitMq description="Blog-RabbitMq" version="1.0"
ADD target/Blog-RabbitMq-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE  8084
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "-Xms64M","-Xmx64M", "-Xmn32M", "-Xss256K","app.jar"]
