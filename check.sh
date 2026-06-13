#!/bin/bash
# ==========================================
# 项目自检脚本
# ==========================================

set -e

# 加载环境变量
if [ -f .env ]; then
    export $(grep -v '^#' .env | grep -v '^$' | xargs)
fi

echo "=========================================="
echo "  项目自检报告"
echo "=========================================="
echo ""

echo "📁 项目名称: ${COMPOSE_PROJECT_NAME}"
echo "🌍 镜像仓库: ${DOCKER_REGISTRY:-DockerHub (默认)}"
echo ""

echo "🔌 端口分配表："
echo "  前端 (Nginx):      ${FRONTEND_PORT}"
echo "  后端 (SpringBoot): ${BACKEND_PORT}"
echo "  MySQL:             ${MYSQL_PORT}"
echo "  Redis:             ${REDIS_PORT}"
echo ""

echo "🔍 端口占用检查："

check_port_status() {
    local port=$1
    local name=$2
    if lsof -nP -iTCP:${port} -sTCP:LISTEN > /dev/null 2>&1; then
        echo "  ✅ ${name} (${port}): 运行中"
        lsof -nP -iTCP:${port} -sTCP:LISTEN 2>/dev/null | awk 'NR>1 {print "     PID: " $2 " 进程: " $1}' | head -1
    else
        echo "  ⚪ ${name} (${port}): 未启动"
    fi
}

check_port_status ${FRONTEND_PORT} "前端"
check_port_status ${BACKEND_PORT} "后端"
check_port_status ${MYSQL_PORT} "MySQL"
check_port_status ${REDIS_PORT} "Redis"

echo ""

if lsof -nP -iTCP:${FRONTEND_PORT} -sTCP:LISTEN > /dev/null 2>&1; then
    echo "🌐 页面访问验证："
    
    # 127.0.0.1
    HTTP_127=$(curl -sS -o /dev/null -w "%{http_code}" http://127.0.0.1:${FRONTEND_PORT} 2>/dev/null || echo "000")
    TITLE_127=$(curl -sS http://127.0.0.1:${FRONTEND_PORT} 2>/dev/null | grep -o '<title>[^<]*</title>' | head -1 | sed 's/<[^>]*>//g' || echo "N/A")
    
    # localhost
    HTTP_LOCAL=$(curl -sS -o /dev/null -w "%{http_code}" http://localhost:${FRONTEND_PORT} 2>/dev/null || echo "000")
    TITLE_LOCAL=$(curl -sS http://localhost:${FRONTEND_PORT} 2>/dev/null | grep -o '<title>[^<]*</title>' | head -1 | sed 's/<[^>]*>//g' || echo "N/A")
    
    echo "  http://127.0.0.1:${FRONTEND_PORT}"
    echo "    HTTP: ${HTTP_127}  标题: ${TITLE_127}"
    echo "  http://localhost:${FRONTEND_PORT}"
    echo "    HTTP: ${HTTP_LOCAL}  标题: ${TITLE_LOCAL}"
    
    if [ "$HTTP_127" = "200" ] && [ "$HTTP_LOCAL" = "200" ] && [ "$TITLE_127" = "$TITLE_LOCAL" ]; then
        echo "  ✅ 两者访问完全一致"
    else
        echo "  ❌ 访问不一致，请检查配置"
    fi
fi

echo ""

echo "🐳 Docker 容器状态："
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || docker ps --filter "name=${COMPOSE_PROJECT_NAME}"

echo ""
echo "=========================================="
