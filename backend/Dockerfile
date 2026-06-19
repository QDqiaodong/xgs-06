# ==========================================
# 后端 Dockerfile - Spring Boot 3.3 + JDK 17
# 严格使用 Docker 原生 Layer 分层缓存
# 禁止使用 # syntax=docker/dockerfile:* 语法
# ==========================================

ARG DOCKER_REGISTRY

# ==========================================
# 构建阶段：Maven 构建
# 分层策略：先 pom.xml 依赖下载，再源码编译
# ==========================================
FROM ${DOCKER_REGISTRY}maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 第一步：仅复制 pom.xml，利用 Docker 缓存
# pom.xml 无变更时，此层及以下依赖层全部复用缓存
COPY pom.xml .

# 预热 Maven 依赖和打包插件，仅 pom.xml 变更时才重新执行
RUN mkdir -p src/main/java && \
    printf '%s\n' 'public class DockerCacheWarmup { public static void main(String[] args) {} }' > src/main/java/DockerCacheWarmup.java && \
    mvn package -Dmaven.test.skip=true -B -e \
    -Dmaven.repo.local=/root/.m2/repository && \
    rm -rf src target

# 第二步：复制源码
# 仅源码修改时，从这里开始重新执行
COPY src ./src

# 编译打包（跳过测试，加快构建）
RUN mvn package -Dmaven.test.skip=true -B -e \
    -Dmaven.repo.local=/root/.m2/repository

# ==========================================
# 运行阶段：JRE 精简镜像
# ==========================================
FROM ${DOCKER_REGISTRY}eclipse-temurin:17-jre-jammy

WORKDIR /app

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone

# 仅复制构建好的 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 容器内部监听端口
EXPOSE 19034

# 启动命令
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dfile.encoding=UTF-8", \
    "-jar", \
    "app.jar"]
