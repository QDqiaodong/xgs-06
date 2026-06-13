# ==========================================
# 前端 Dockerfile - Vue 3 + Vite + Nginx
# 严格使用 Docker 原生 Layer 分层缓存
# 禁止使用 # syntax=docker/dockerfile:* 语法
# ==========================================

ARG DOCKER_REGISTRY

# ==========================================
# 构建阶段：Node.js 编译
# 分层策略：先 package.json + .npmrc 安装依赖，再源码编译
# ==========================================
FROM ${DOCKER_REGISTRY}node:20-alpine AS builder

WORKDIR /app

# 第一步：仅复制包管理文件和镜像源配置
# package.json / package-lock.json / .npmrc 无变更时，此层及以下依赖层全部复用缓存
COPY package*.json ./
COPY .npmrc ./

# 安装依赖，仅包管理文件变更时才重新执行
# 使用 npm ci 严格按照 package-lock.json 安装，版本锁定更稳定
RUN npm ci --prefer-offline --no-audit --no-fund

# 第二步：复制源码
# 仅源码修改时，从这里开始重新执行
COPY . .

# 编译打包
RUN npm run build

# ==========================================
# 运行阶段：Nginx 静态资源服务
# ==========================================
FROM ${DOCKER_REGISTRY}nginx:alpine

WORKDIR /usr/share/nginx/html

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone

# 仅复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 容器内部监听端口
EXPOSE 80

# Nginx 前台运行
CMD ["nginx", "-g", "daemon off;"]
