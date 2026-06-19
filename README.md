# 🧶 手工皮具创作分享平台

面向手工皮具爱好者打造的内容社区平台，用户分享皮具裁料、缝制、上色等制作流程，展示成品样式与工艺细节，交流手工技法与用料选择。

---

## 📋 目录

- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [端口分配表](#端口分配表)
- [快速启动](#快速启动)
- [Docker 构建优化说明](#docker-构建优化说明)
- [目录结构](#目录结构)
- [配置说明](#配置说明)
- [开发指南](#开发指南)

---

## 🛠️ 技术栈

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| **前端** | Vue 3 + Vite + Vant 4 + Pinia + Vue Router | Vue 3.4, Vite 5.2 |
| **后端** | Spring Boot 3.3 + JDK 17 + MyBatis-Plus | Spring Boot 3.3.0 |
| **数据库** | MySQL 8.0 + Redis 7 | MySQL 8.0, Redis 7 |
| **部署** | Docker + Docker Compose + Nginx | Docker Compose 3.8 |

---

## ✨ 功能模块

### 核心功能
1. **作品流程发布**: 上传制作过程图与成品图，记录用料、工序、制作心得，选择皮具品类完成发布
2. **内容分类浏览**: 按钱包、卡包、皮带等品类，或是新手教程、进阶工艺筛选内容
3. **作品收藏汇总**: 收藏优质作品与教程，整理成个人工艺参考库
4. **个人作品管理**: 集中查看本人所有发布内容，支持编辑文案、下架旧作品

### 系统特性
- 前端图片分组懒加载
- 分步式创作表单组件
- 打包阶段资源压缩
- Redis 缓存高人气作品
- MySQL 专属数据卷持久化
- 容器化一键部署
- 全链路国内镜像源加速

---

## 🔌 端口分配表

> 所有端口均已避开常用默认端口，统一在 `.env` 文件管理

| 服务 | 宿主机端口 | 容器内端口 | 绑定地址 | 说明 |
|------|----------|----------|---------|------|
| **前端 (Nginx)** | 18034 | 80 | 127.0.0.1 | 避开 80, 8080 |
| **后端 (Spring Boot)** | 19034 | 19034 | 127.0.0.1 | 避开 8080 |
| **MySQL** | 13334 | 3306 | 127.0.0.1 | 避开 3306 |
| **Redis** | 16334 | 6379 | 127.0.0.1 | 避开 6379 |

### 端口约束
- 所有服务严格绑定 IPv4 回环地址 `127.0.0.1`，禁止外网访问
- 端口固定，端口冲突时明确报错，不自动切换
- `localhost` 与 `127.0.0.1` 访问完全一致

---

## 🚀 快速启动

### 方式一：一键启动脚本（推荐）

```bash
./start.sh
```

脚本自动完成：端口检测 → 镜像构建 → 服务启动 → 健康检查 → 一致性验证 → 输出访问地址

### 方式二：Docker Compose 原生命令

```bash
# 构建并启动
docker compose up -d

# 查看启动日志
docker compose logs -f

# 重新构建（源码修改时仅重编译，依赖不变更复用缓存
docker compose up --build -d

# 停止服务
docker compose down
```

### 访问地址

构建启动成功后访问：

- **前端**: http://localhost:18034
- **前端**: http://127.0.0.1:18034
- **后端 API**: http://127.0.0.1:19034/api

### 测试账号

- 用户名: `admin`
- 密码: `123456`

---

## 📦 Docker 构建优化说明

### 一、依赖源国内镜像

#### 前端 NPM
- 镜像源：`https://registry.npmmirror.com
- 配置文件：`frontend/.npmrc
- 无需 VPN 即可正常拉取

#### 后端 Maven
- 阿里云公共库：阿里云 Maven 镜像
- 配置：`backend/pom.xml` 内配置 repositories
- 包含 public / central / spring 三个仓库源

### 二、分层缓存机制（严格遵守）

#### 前端 Dockerfile 分层策略：
```
┌─────────────────────────────────────────────┐
│  Layer 1: COPY package*.json + .npmrc        │ ← 包管理文件变更时才重建
├─────────────────────────────────────────────┤
│  Layer 2: RUN npm ci                      │ ← 依赖下载，复用缓存
├─────────────────────────────────────────────┤
│  Layer 3: COPY . . (源码)                │ ← 仅源码修改时从这里重建
├─────────────────────────────────────────────┤
│  Layer 4: RUN npm run build                   │ ← 仅重新编译
└─────────────────────────────────────────────┘
```

#### 后端 Dockerfile 分层策略：
```
┌─────────────────────────────────────────────┐
│  Layer 1: COPY pom.xml                   │ ← pom.xml 变更时才重建
├─────────────────────────────────────────────┤
│  Layer 2: RUN mvn dependency:go-offline  │ ← 依赖下载，复用缓存
├─────────────────────────────────────────────┤
│  Layer 3: COPY src ./src                 │ ← 仅源码修改时从这里重建
├─────────────────────────────────────────────┤
│  Layer 4: RUN mvn clean package              │ ← 仅重新编译打包
└─────────────────────────────────────────────┘
```

### 三、全局镜像仓库 DOCKER_REGISTRY

所有基础镜像统一引用全局环境变量：
- `node:20-alpine`
- `nginx:alpine`
- `maven:3.9.6-eclipse-temurin-17-alpine`
- `eclipse-temurin:17-jre-alpine`
- `mysql:8.0`
- `redis:7-alpine`

如需切换镜像源，只需修改 `.env` 文件中 `DOCKER_REGISTRY` 变量：
```env
# 示例：使用中科大镜像
DOCKER_REGISTRY=docker.mirrors.ustc.edu.cn/

# 示例：使用阿里云镜像
DOCKER_REGISTRY=registry.cn-hangzhou.aliyuncs.com/

# 留空使用 DockerHub（默认）
DOCKER_REGISTRY=
```

---

## 📂 目录结构

```
.
├── .env                         # 全局环境变量配置（端口、镜像源等）
├── .gitignore
├── docker-compose.yml            # Docker Compose 编排
├── start.sh                     # 一键启动脚本
├── check.sh                     # 项目自检脚本
├── README.md                    # 项目说明文档
│
├── backend/                      # 后端项目
│   ├── Dockerfile               # 后端镜像构建（分层缓存）
│   ├── pom.xml                  # Maven 依赖 + 阿里云镜像源
│   └── src/main/
│       ├── java/com/leathercraft/
│       │   ├── controller/         # 控制层
│       │   ├── service/            # 服务层接口 + impl
│       │   ├── mapper/             # MyBatis 数据访问
│       │   ├── entity/             # 实体类
│       │   ├── dto/                # 数据传输对象
│       │   ├── config/             # 配置类
│       │   └── common/             # 通用工具
│       └── resources/
│           ├── application.yml      # 应用配置
│           └── db/schema.sql      # 数据库初始化脚本
│
└── frontend/                # 前端项目
    ├── Dockerfile               # 前端镜像构建（分层缓存）
    ├── .npmrc                   # NPM 国内镜像源
    ├── nginx.conf               # Nginx 配置
    ├── vite.config.js           # Vite 配置（绑定 127.0.0.1:18034）
    ├── package.json             # NPM 依赖
    └── src/
        ├── views/               # 页面组件
        ├── components/          # 通用组件
        ├── router/              # 路由配置
        ├── store/               # Pinia 状态管理
        ├── api/                 # API 接口
        ├── utils/               # 工具函数
        └── assets/              # 静态资源
```

---

## ⚙️ 配置说明

### .env 环境变量

```env
# 项目名称
COMPOSE_PROJECT_NAME=leather-craft-platform

# Docker 镜像仓库（统一配置
DOCKER_REGISTRY=

# 端口配置
FRONTEND_PORT=18034
BACKEND_PORT=19034
MYSQL_PORT=13334
REDIS_PORT=16334

# MySQL 配置
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=leather_craft
MYSQL_USER=leather
MYSQL_PASSWORD=leather123

# 时区
TZ=Asia/Shanghai
```

### Vite 开发服务器配置

严格遵守：
```javascript
server: {
  host: '127.0.0.1',    // 仅绑定 IPv4 回环地址
  port: 18034,            // 固定端口
  strictPort: true        // 端口占用直接报错
}
```

---

## 👨‍💻 开发指南

### 本地开发

#### 后端开发
```bash
cd backend
mvn spring-boot:run
```

#### 前端开发
```bash
cd frontend
npm install
npm run dev
```

### 项目自检

```bash
./check.sh
```

自动检查：
- 端口占用情况
- 容器运行状态
- localhost 与 127.0.0.1 访问一致性

### 常见问题

**Q: 端口被占用怎么办？**
A: 运行 `lsof -nP -iTCP:<PORT> -sTCP:LISTEN` 查看占用进程，停止后重试，或修改 `.env` 中端口配置

**Q: 镜像拉取失败？**
A: 修改 `.env` 中 `DOCKER_REGISTRY` 配置国内镜像源

**Q: 如何验证 localhost 和 127.0.0.1 访问不一致？**
A: 检查 hosts 文件，确保没有 IPv6 监听，所有服务仅绑定 127.0.0.1

---

## 📝 版本历史

- v1.0.0 - 初始版本，全栈完整功能实现

---

## 📄 许可证

MIT License
