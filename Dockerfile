FROM openjdk:17-ea-slim-buster

## 设置时区
RUN mkdir /app && apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 设置工作目录
WORKDIR /vxph
VOLUME /vxph

# 复制构建好的应用程序到新的镜像中
COPY build/libs /app

# 暴露容器的端口
EXPOSE 8018

# 启动应用程序
CMD [ "java", "-jar", "-Djava.net.preferIPv6Addresses=true", "-Dproperties.path=/vxph/vxph.properties", "/app/vxph-1.0.0-SNAPSHOT-fat.jar" ]
