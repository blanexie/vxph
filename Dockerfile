# 创建一个新的镜像，基于构建镜像
FROM gradle:7.6.3-jdk17

# 配置环境变量
ENV PORT=8016
ENV appJarName = "vxph-1.0.0-SNAPSHOT-fat.jar"

COPY build.gradle.kts /app/build.gradle.kts
COPY settings.gradle.kts /app/settings.gradle.kts
COPY src /app/src
COPY db /app/db


# 设置工作目录
WORKDIR /app

# 使用 Gradle 构建项目
RUN gradle clean build --no-daemon

# 复制构建好的应用程序到新的镜像中
#COPY --from=build /app/build/libs/$appJarName  /app/build/libs/$appJarName

# 暴露容器的端口
EXPOSE 8016

# 启动应用程序
CMD ["java", "-jar", "/app/build/libs/${appJarName}"]
