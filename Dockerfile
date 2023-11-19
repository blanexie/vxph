FROM openjdk:17-ea-slim-buster

ENV TZ="Asia/Shanghai"

VOLUME /app
WORKDIR /app

COPY ./build/libs/vxph-0.0.1-SNAPSHOT.jar /app/vxph.jar

EXPOSE 8018

CMD ["java -jar  /app/vxph.jar" ]
