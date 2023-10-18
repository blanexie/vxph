#!/bin/bash

# 阿里云 Docker 镜像仓库的用户名和密码
username="765150816@qq.com"
password="111111111"
registry="registry.cn-hangzhou.aliyuncs.com"

# 登录到阿里云 Docker 镜像仓库
if docker login -u "$username" -p "$password" $registry; then
    echo "登录成功"
else
    echo "登录失败, 退出脚本执行"
    exit 1
fi

## 进入工作目录
cd ..

## 拉取最新的代码
git  pull

echo "开始构建gradle的镜像，后面使用此镜像来编译项目"
## 判断gradle编译镜像是否存在， 不存在重新构建
if docker images -q  gradle-image ; then
    echo "gradle-image 镜像不存在，开始重新构建"
    docker build -t gradle-image -f docker/Dockerfile-Gradle .
else
    echo "gradle-image已经存在直接下一步"
fi

echo "开始使用gradle-image镜像来编译项目"
## 判断编译容器是否已经存在， 存在则restart下， 不存在则run下
if docker ps -a | grep -q "gradle-run"; then
    echo "之前已经启动过容器， 现在重新启动下，由于目录是映射的，所以没有 问题"
    docker restart gradle-run
else
    echo "之前没启动过容器，现在重新开始run , 配置映射目录"
    docker run -it -v /gradle:/home/gradle/.gradle -v "$(pwd)":/app --name gradle-run gradle-image
fi

echo "项目已经编译打包成jar包了， 现在开始构建运行镜像"
## 获取最新提交的git hash
version=$(git rev-parse HEAD)

# 构建 Docker 镜像
docker build -f docker/Dockerfile -t ${registry}/blane/vxph:${version} .

# 推送 Docker 镜像到阿里云个人仓库
docker push  ${registry}/blane/vxph:${version}
