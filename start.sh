#!/bin/bash
# ==========================================
# 手工皮具创作分享平台 - 一键启动脚本
# ==========================================

set -e

# 加载环境变量
if [ -f .env ]; then
    export $(grep -v '^#' .env | grep -v '^$' | xargs)
fi

echo "=========================================="
echo "  手工皮具创作分享平台 - 启动中"
echo "=========================================="
echo ""

# ==========================================
# 端口冲突检测
# ==========================================
echo "[1/5] 检测端口占用情况..."

check_port() {
    local port=$1
    local name=$2
    if lsof -nP -iTCP:${port} -sTCP:LISTEN > /dev/null 2>&1; then
        echo "❌  端口 ${port} (${name}) 已被占用！"
        echo "   占用进程信息："
        lsof -nP -iTCP:${port} -sTCP:LISTEN | head -5
        echo ""
        echo "请先停止占用端口的进程，或修改 .env 文件中的端口配置"
        exit 1
    else
        echo "✅  端口 ${port} (${name}) 可用"
    fi
}

check_port ${FRONTEND_PORT} "前端"
check_port ${BACKEND_PORT} "后端"
check_port ${MYSQL_PORT} "MySQL"
check_port ${REDIS_PORT} "Redis"

echo ""

# ==========================================
# 拉取并构建镜像
# ==========================================
echo "[2/5] 构建 Docker 镜像（首次构建较慢，请耐心等待）..."
echo "  - 国内镜像源已配置，无需 VPN"
echo "  - 分层缓存优化，后续构建速度更快"
echo ""

docker compose build

echo ""

# ==========================================
# 启动服务
# ==========================================
echo "[3/5] 启动容器服务..."
docker compose up -d

echo ""

# ==========================================
# 等待服务就绪
# ==========================================
echo "[4/5] 等待服务启动就绪（约需 30-60 秒）..."

wait_for_service() {
    local name=$1
    local port=$2
    local max_retries=30
    local count=0

    echo -n "  等待 ${name} 启动"
    while ! lsof -nP -iTCP:${port} -sTCP:LISTEN > /dev/null 2>&1; do
        count=$((count + 1))
        if [ $count -ge $max_retries ]; then
            echo ""
            echo "❌  ${name} 启动超时！请检查日志：docker compose logs ${name}"
            exit 1
        fi
        echo -n "."
        sleep 2
    done
    echo ""
    echo "  ✅ ${name} 已就绪"
}

wait_for_service "MySQL" ${MYSQL_PORT}
wait_for_service "Redis" ${REDIS_PORT}
wait_for_service "后端" ${BACKEND_PORT}
wait_for_service "前端" ${FRONTEND_PORT}

echo ""

# ==========================================
# 验证访问一致性
# ==========================================
echo "[5/5] 验证访问一致性..."

# 验证 127.0.0.1
HTTP_127=$(curl -sS -o /dev/null -w "%{http_code}" http://127.0.0.1:${FRONTEND_PORT} || echo "000")
TITLE_127=$(curl -sS http://127.0.0.1:${FRONTEND_PORT} 2>/dev/null | grep -o '<title>[^<]*</title>' | head -1 || echo "")

# 验证 localhost
HTTP_LOCAL=$(curl -sS -o /dev/null -w "%{http_code}" http://localhost:${FRONTEND_PORT} || echo "000")
TITLE_LOCAL=$(curl -sS http://localhost:${FRONTEND_PORT} 2>/dev/null | grep -o '<title>[^<]*</title>' | head -1 || echo "")

if [ "$HTTP_127" = "200" ] && [ "$HTTP_LOCAL" = "200" ] && [ "$TITLE_127" = "$TITLE_LOCAL" ]; then
    echo "  ✅ 127.0.0.1:${FRONTEND_PORT} - HTTP ${HTTP_127} - ${TITLE_127}"
    echo "  ✅ localhost:${FRONTEND_PORT}   - HTTP ${HTTP_LOCAL} - ${TITLE_LOCAL}"
    echo "  ✅ 两者访问一致"
else
    echo "  ❌ 访问验证失败！"
    echo "  127.0.0.1: HTTP ${HTTP_127} Title: ${TITLE_127}"
    echo "  localhost:   HTTP ${HTTP_LOCAL} Title: ${TITLE_LOCAL}"
fi

echo ""

# ==========================================
# 输出访问地址
# ==========================================
echo "=========================================="
echo "  🎉 平台启动成功！"
echo "=========================================="
echo ""
echo "  🌐 前端访问地址："
echo "     http://localhost:${FRONTEND_PORT}"
echo "     http://127.0.0.1:${FRONTEND_PORT}"
echo ""
echo "  🔧 后端 API 地址："
echo "     http://127.0.0.1:${BACKEND_PORT}/api"
echo ""
echo "  🗄️  MySQL 数据库："
echo "     Host: 127.0.0.1  Port: ${MYSQL_PORT}"
echo "     User: root  Password: ${MYSQL_ROOT_PASSWORD}"
echo ""
echo "  💾 Redis 缓存："
echo "     Host: 127.0.0.1  Port: ${REDIS_PORT}"
echo ""
echo "  👤 测试账号："
echo "     用户名: admin"
echo "     密码: 123456"
echo ""
echo "  📋 常用命令："
echo "     查看日志: docker compose logs -f"
echo "     停止服务: docker compose down"
echo "     重启服务: docker compose restart"
echo "     重新构建: docker compose up --build -d"
echo ""
echo "=========================================="
