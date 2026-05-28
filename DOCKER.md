# Daily Diary — Docker 部署文档

## 1. 项目简介

Daily Diary 是一个简洁优雅的在线日记系统，支持多用户注册登录、日记的增删改查、心情/天气标记、按月归档等功能。系统采用前后端分离架构：

- **后端**：Java Spring Boot 2.7 + MyBatis-Plus + Spring Security + JWT
- **前端**：Vue 3 + Element Plus + Vite
- **数据库**：MySQL 8.0

---

## 2. 环境要求

| 组件 | 版本要求 |
|------|---------|
| Docker | 20.10+ |
| Docker Compose | 2.0+ |

---

## 3. 快速开始

### 3.1 克隆项目

```bash
# 确保项目目录结构如下:
# .
# ├── diary-backend/      # Spring Boot 后端
# ├── diary-frontend/     # Vue 前端
# ├── docker-compose.yml
# ├── nginx.conf
# └── DOCKER.md
```

### 3.2 修改数据库配置

编辑 `diary-backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/diary_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_secure_password_here
```

> 注意：Docker Compose 环境中，数据库主机名为 `mysql`（服务名），不是 `localhost`。

### 3.3 启动服务

```bash
docker-compose up -d
```

首次启动会自动：
1. 拉取 MySQL 8.0 镜像并初始化数据库
2. 编译构建后端 Spring Boot 项目
3. 构建前端 Vue 项目
4. 启动 Nginx 反向代理

### 3.4 验证

- 前端页面：`http://localhost`
- 后端 API：`http://localhost/api/auth/login`

---

## 4. Docker Compose 配置

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: diary-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: your_secure_password_here
      MYSQL_DATABASE: diary_system
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./diary-backend/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./diary-backend
      dockerfile: Dockerfile
    container_name: diary-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/diary_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: your_secure_password_here
      TZ: Asia/Shanghai
    ports:
      - "8080:8080"

  nginx:
    image: nginx:alpine
    container_name: diary-nginx
    restart: always
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./diary-frontend/dist:/usr/share/nginx/html
    depends_on:
      - backend

volumes:
  mysql_data:
```

---

## 5. 后端 Dockerfile

位置：`diary-backend/Dockerfile`

```dockerfile
# 多阶段构建
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:8-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 6. Nginx 配置

位置：项目根目录 `nginx.conf`

```nginx
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript;
    gzip_min_length 1000;

    server {
        listen 80;
        server_name localhost;

        # 前端静态文件
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }

        # 后端 API 代理
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

---

## 7. 环境变量说明

### 后端环境变量 (backend)

| 环境变量 | 默认值 | 说明 |
|---------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/diary_system?...` | MySQL 连接 URL |
| `SPRING_DATASOURCE_USERNAME` | `root` | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | - | 数据库密码（必填） |
| `JWT_SECRET` | `diary-system-secret-key-...` | JWT 签名密钥（生产环境建议修改） |
| `JWT_EXPIRATION` | `86400000` | Token 过期时间（毫秒，默认 24 小时） |
| `SERVER_PORT` | `8080` | 后端服务端口 |

### MySQL 环境变量

| 环境变量 | 说明 |
|---------|------|
| `MYSQL_ROOT_PASSWORD` | Root 密码 |
| `MYSQL_DATABASE` | 自动创建的数据库名 |

---

## 8. 数据持久化

- **MySQL 数据**：通过 Docker Volume `mysql_data` 持久化在宿主机上。即使容器删除，数据不会丢失。
- **查看 Volume**：`docker volume ls | grep mysql_data`
- **备份数据**：
  ```bash
  docker exec diary-mysql mysqldump -u root -p diary_system > backup.sql
  ```

---

## 9. 生产环境建议

1. **修改 JWT Secret**：将 `application.yml` 中的 `jwt.secret` 改为高强度随机字符串
2. **使用 HTTPS**：建议在 Nginx 前加一层反向代理（如 Traefik / Caddy）配置 SSL 证书
3. **数据库密码**：使用 Docker Secrets 或环境变量文件（`.env`）管理敏感信息
4. **日志收集**：挂载日志目录到宿主机，配合 ELK 等日志系统
5. **健康检查**：后端添加 `/actuator/health` 端点（引入 spring-boot-starter-actuator）