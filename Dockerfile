FROM openjdk:17-ea-slim-buster

# 设置工作目录
WORKDIR /app
VOLUME /app

# 配置环境变量
ENV appJarName=vxph-1.0.0-SNAPSHOT-fat.jar
ENV VM_ARG = ""

# 复制构建好的应用程序到新的镜像中
COPY build/libs/$appJarName /app/$appJarName

# 暴露容器的端口
EXPOSE 8016

# 启动应用程序
CMD ["java",  "-jar" , "$VM_ARG" , "$appJarName"]
