FROM openjdk:17-ea-slim-buster

# 设置工作目录
WORKDIR /app
VOLUME /app

## 设置时区
RUN mkdir /app && apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 配置环境变量
ENV appJarName=vxph-1.0.0-SNAPSHOT-fat.jar

# 复制构建好的应用程序到新的镜像中
COPY build/libs/$appJarName /app

# 暴露容器的端口
EXPOSE 8016

# 启动应用程序
CMD [ "java -jar -Djava.net.preferIPv6Addresses=true -Dproperties.path=/app/vxph.properties vxph-1.0.0-SNAPSHOT-fat.jar" ]
