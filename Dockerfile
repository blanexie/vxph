FROM openjdk:17-ea-slim-buster

ENV TZ="Asia/Shanghai"

VOLUME /app
WORKDIR /app

COPY ./build/libs /app

EXPOSE 8018

CMD ["bash" ,"-c", "java -jar  /app/vxph-0.0.1-SNAPSHOT.jar" ]
