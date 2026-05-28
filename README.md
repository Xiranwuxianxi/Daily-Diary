# Daily Diary — 在线日记系统

## 项目简介

Daily Diary 是一个简洁优雅的在线日记系统，支持多用户注册登录、日记的增删改查、心情/天气标记、按月归档等功能。界面采用温暖纸质风格设计，响应式布局适配手机端和桌面端。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7 |
| 安全认证 | Spring Security + JWT |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 3 (Composition API) |
| UI 组件库 | Element Plus |
| 构建工具 | Vite 5 |
| 容器化 | Docker + Docker Compose |

## 功能特性

- 多用户注册登录（JWT 认证）
- 日记增删改查
- 标题搜索、心情筛选、日期范围筛选
- 心情标记（开心/伤心/生气/平静/思考/幸福）
- 天气标记（晴/多云/雨/雪/风）
- 按月归档统计
- 响应式设计（手机端/平板端/桌面端适配）
- 温暖纸张质感配色

## 项目结构

```
output/
├── diary-backend/                   # Spring Boot 后端项目
│   ├── pom.xml
│   ├── src/main/java/com/diary/
│   │   ├── DiaryApplication.java    # 启动类
│   │   ├── common/                  # 通用类（统一响应、分页结果）
│   │   ├── config/                  # 配置（Security、CORS、MyBatis-Plus、密码加密）
│   │   ├── entity/                  # 实体类（User、Diary）
│   │   ├── mapper/                  # MyBatis 映射接口
│   │   ├── service/                 # 业务逻辑层
│   │   ├── controller/              # 控制器
│   │   ├── dto/                     # 数据传输对象
│   │   └── utils/                   # 工具类（JWT）
│   └── src/main/resources/
│       ├── application.yml          # 应用配置
│       ├── schema.sql               # 数据库初始化脚本
│       └── mapper/DiaryMapper.xml   # 自定义 SQL
├── diary-frontend/                  # Vue 3 前端项目
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── router/index.js          # 路由配置
│       ├── api/                     # API 封装
│       │   ├── request.js           # axios 实例与拦截器
│       │   ├── auth.js              # 认证 API
│       │   └── diary.js             # 日记 API
│       ├── views/                   # 页面视图
│       │   ├── Login.vue            # 登录/注册页
│       │   ├── DiaryList.vue        # 日记列表页
│       │   ├── DiaryWrite.vue       # 写日记/编辑页
│       │   └── DiaryDetail.vue      # 日记详情页
│       ├── components/              # 组件
│       │   ├── AppHeader.vue        # 顶部导航栏
│       │   └── MoodPicker.vue       # 心情选择器
│       └── assets/style.css         # 全局样式
├── docker-compose.yml               # Docker Compose 编排
├── nginx.conf                       # Nginx 配置
├── DOCKER.md                        # Docker 部署文档
└── README.md                        # 本文件
```

## 本地开发指南

### 后端

1. 确保已安装 JDK 8+ 和 Maven 3.6+
2. 创建 MySQL 数据库并执行 `schema.sql`
3. 修改 `application.yml` 中的数据库连接信息
4. 运行：
   ```bash
   cd diary-backend
   mvn spring-boot:run
   ```

### 前端

1. 确保已安装 Node.js 18+
2. 安装依赖并启动：
   ```bash
   cd diary-frontend
   npm install
   npm run dev
   ```
3. 浏览器访问 `http://localhost:3000`

### 默认配置

- 后端端口：`8080`
- 前端开发端口：`3000`
- 前端自动代理 `/api` 到后端 `8080`

## Docker 部署

详细部署步骤请参考 [DOCKER.md](./DOCKER.md)。

## API 文档概要

### 认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/login` | 用户登录 | 否 |
| GET | `/api/auth/me` | 获取当前用户信息 | 是 |

### 日记接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/diary` | 分页查询日记列表 | 是 |
| GET | `/api/diary/{id}` | 查询单条日记 | 是 |
| POST | `/api/diary` | 新增日记 | 是 |
| PUT | `/api/diary/{id}` | 更新日记 | 是 |
| DELETE | `/api/diary/{id}` | 删除日记 | 是 |
| GET | `/api/diary/archive` | 按月归档统计 | 是 |

### 请求格式

所有 POST/PUT 请求使用 JSON 格式，Content-Type: application/json。

### 认证方式

登录成功后返回 JWT token，后续请求在 Header 中携带：
```
Authorization: Bearer <token>
```

## 配色说明

本项目严格使用温暖纸张质感配色方案：

- 主背景：柔白 `#FAFAF7`
- 强调色：琥珀金 `#C8956C`
- 危险色：柔和玫瑰红 `#D4786C`
- 不使用任何蓝紫色系（`#1890ff, #409EFF, #3B82F6, #6366F1, #8B5CF6, #7C3AED` 等）