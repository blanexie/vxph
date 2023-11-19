FROM openjdk:17-ea-slim-buster

ENV TZ="Asia/Shanghai"

VOLUME /app
WORKDIR /app

COPY ./build/libs/vxph-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8018


ENTRYPOINT ["java"]
CMD [" -jar  /app/vxph-0.0.1-SNAPSHOT.jar" ]
