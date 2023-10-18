#!/bin/bash

# 阿里云 Docker 镜像仓库的用户名和密码
username="765150816@qq.com"
password="111111111"
registry="registry.cn-hangzhou.aliyuncs.com/blane"

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

gradleBuildImage="gradle-build:1.0"
gradleBuild="gradle-build"

## 判断gradle编译镜像是否存在， 不存在重新构建
if docker images -q "$gradleBuildImage" >/dev/null 2>&1; then
    echo "gradle 编译镜像已经存在$gradleBuildImage"
else
    echo "gradle 构建编译镜像$gradleBuildImage"
    docker build -t $gradleBuildImage -f docker/Dockerfile .
fi

## 判断编译容器是否已经存在， 存在则restart下， 不存在则run下
# 检查容器是否存在
if docker ps -a | grep -q "$gradleBuild"; then
    echo "容器已存在，restart下"
    docker restart $gradleBuild
else
    echo "容器不存在， run一下"
    docker run -it -v ../gradle:/home/gradle/.gradle -v $(pwd):/app --name $gradleBuild $gradleBuild
fi

echo "开够构建项目运行镜像"

## 获取最新提交的git hash
version=$(git rev-parse HEAD)

# 构建 Docker 镜像
docker build -f docker/Dockerfile -t ${registry}/vxph:$version .

# 推送 Docker 镜像到阿里云个人仓库
docker push  ${registry}/vxph:$version
