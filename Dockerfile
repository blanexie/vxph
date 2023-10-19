FROM gradle:7.6.3-jdk17 as build

WORKDIR /app

COPY db /app/db
COPY src /app/src
COPY build.gradle.kts /app/build.gradle.kts
COPY gradle.properties /app/gradle.properties
COPY settings.gradle.kts /app/settings.gradle.kts
COPY gradlew /app/gradlew

RUN gradle clean build


# 创建一个新的镜像，基于构建镜像
FROM openjdk:17-ea-slim-buster
# 设置工作目录
WORKDIR /app
# 配置环境变量
ENV appJarName=vxph-shadow-1.0.0-SNAPSHOT.tar
# 复制构建好的应用程序到新的镜像中
COPY --from=build /app/build/distributions/$appJarName /app/$appJarName

RUN tar -xvf $appJarName

# 暴露容器的端口
EXPOSE 8016

# 启动应用程序
CMD ["sh",  "/app/vxph-shadow-1.0.0-SNAPSHOT/bin/vxph" ]
