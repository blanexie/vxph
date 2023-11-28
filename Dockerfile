FROM openjdk:17-ea-slim-buster

ENV TZ="Asia/Shanghai"

VOLUME /data

COPY ./build/libs/vxph-0.0.1-SNAPSHOT.jar /vxph.jar

EXPOSE 8018


ENTRYPOINT ["java"]
CMD ["-jar /vxph.jar"]
