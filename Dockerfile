
FROM openjdk:17-ea-slim-buster

ADD build/distributions/vxph-shadow-1.0.0-SNAPSHOT.tar   app/

# 暴露容器的端口
EXPOSE 8016

# 启动应用程序
CMD ["sh",  "/app/vxph-shadow-1.0.0-SNAPSHOT/bin/vxph" ]
