# 响应日志功能说明

## 已添加的功能

### 1. 请求/响应日志切面 (WebLogAspect)
- **自动记录所有Controller的请求和响应**
- 使用AOP技术,无需修改现有Controller代码
- 记录内容包括:
  - 请求地址和HTTP方法
  - 当前登录用户ID
  - 请求参数(JSON格式)
  - 响应结果(JSON格式)
  - 接口执行耗时

### 2. 全局异常处理器 (GlobalExceptionHandler)
- **统一捕获和处理所有异常**
- 记录详细的错误堆栈信息
- 返回友好的错误提示给前端
- 支持多种异常类型:
  - 业务异常(RuntimeException)
  - 参数校验异常(IllegalArgumentException)
  - 权限不足异常(AccessDeniedException)
  - 空指针异常(NullPointerException)
  - 其他所有异常(Exception)

### 3. 日志配置 (logback-spring.xml)
- **彩色控制台输出**
- 优化的日志格式: `时间 [线程] 日志级别 类名 - 消息`
- 可配置的日志级别:
  - 应用日志: DEBUG (显示所有请求/响应日志)
  - Spring框架: WARN (减少噪音)
  - Spring Security: INFO (查看认证信息)
  - MyBatis-Plus: DEBUG (查看SQL语句)

## 日志示例

```
2024-01-15 10:30:15.123 [http-nio-8888-exec-5] INFO  com.diary.controller.DiaryController - ========================================
2024-01-15 10:30:15.123 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - 请求地址: GET /api/diary
2024-01-15 10:30:15.123 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - 用户ID: 123
2024-01-15 10:30:15.123 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - 请求参数: page=1 size=10 keyword= mood= startDate= endDate=
2024-01-15 10:30:15.123 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - ========================================
2024-01-15 10:30:15.234 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - ----------------------------------------
2024-01-15 10:30:15.234 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - 响应结果 (耗时: 111ms):
2024-01-15 10:30:15.234 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - {"code":200,"message":"success","data":{"records":[...],"total":100,...}}
2024-01-15 10:30:15.234 [http-nio-8888-exec-5] INFO  com.diary.config.WebLogAspect - ----------------------------------------

2024-01-15 10:31:20.456 [http-nio-8888-exec-8] ERROR com.diary.config.GlobalExceptionHandler - 业务异常:
java.lang.RuntimeException: 日记不存在或无权访问
    at com.diary.service.impl.DiaryServiceImpl.getById(DiaryServiceImpl.java:45)
    ...
```

## 如何使用

### 启动后端服务

```bash
cd diary-backend
mvn spring-boot:run
```

### 查看实时日志

启动后,所有API请求都会在控制台显示详细的请求和响应信息。

### 调整日志级别

如需调整日志详细程度,可以修改 `logback-spring.xml`:

```xml
<!-- 将应用日志级别改为INFO,减少输出 -->
<logger name="com.diary" level="INFO"/>

<!-- 或改为TRACE,查看最详细的日志 -->
<logger name="com.diary" level="TRACE"/>
```

### 查看SQL语句

日志配置已开启MyBatis-Plus的SQL日志,可以查看执行的SQL:

```xml
<logger name="com.diary.mapper" level="DEBUG"/>
```

## 文件清单

### 新增文件
- `diary-backend/src/main/java/com/diary/config/WebLogAspect.java` - 请求/响应日志切面
- `diary-backend/src/main/java/com/diary/config/GlobalExceptionHandler.java` - 全局异常处理器
- `diary-backend/src/main/resources/logback-spring.xml` - 日志配置文件

### 修改文件
- `diary-backend/pom.xml` - 添加AspectJ和Jackson依赖
- `diary-backend/src/main/java/com/diary/common/R.java` - 添加failed方法

## 注意事项

1. **开发环境使用**: 此日志配置适合开发环境,生产环境可以调整日志级别以减少输出
2. **性能影响**: AOP切面会有轻微的性能开销(约1-5ms),开发环境可接受,生产环境可按需关闭
3. **敏感信息**: 日志会记录请求参数,请注意不要包含密码等敏感信息(目前密码只在登录时传输,已加密)
4. **日志大小**: 长时间运行会生成大量日志,生产环境建议配置日志轮转

## 关闭日志功能

如需临时关闭日志,可以:

1. 注释掉 `WebLogAspect.java` 的 `@Aspect` 注解
2. 或修改 `logback-spring.xml` 将 `com.diary` 日志级别改为 `ERROR`

## 故障排查

如果看不到日志:
1. 检查 `logback-spring.xml` 是否在 `src/main/resources/` 目录下
2. 检查日志级别是否设置正确
3. 检查控制台输出是否被重定向到文件
