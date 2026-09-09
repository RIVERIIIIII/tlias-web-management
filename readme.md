# Tlias 智能学习管理系统（后端）

基于 Spring Boot 的教学管理系统后端，提供部门、员工、班级、学生、统计报表、文件上传和操作日志等接口。

## 技术栈

- Java 17
- Spring Boot 3.5.6
- MyBatis 3.0.5、PageHelper
- MySQL 8.0
- Redis、Spring Cache
- JWT
- Spring AOP、Spring Transaction
- 阿里云 OSS
- Maven

Docker 容器化尚未接入，作为后续进阶开发内容。

## 环境要求

- JDK 17
- Maven 3.9 或使用项目自带 Maven Wrapper
- MySQL 8.0
- Redis 6.2+（推荐 Redis 7）
- Nginx（仅运行配套前端时需要）
- 已创建并可访问的阿里云 OSS Bucket（仅测试上传接口时需要）

## 初始化数据库

1. 启动 MySQL。
2. 创建 `tlias` 数据库：

```sql
CREATE DATABASE IF NOT EXISTS tlias
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
```

3. 使用仓库根目录的 `schema.sql` 初始化表结构：

```shell
mysql -u root -p tlias < schema.sql
```

`schema.sql` 只包含表结构，不包含业务数据。首次运行前需要自行准备一个可登录的员工账号。

## 配置环境变量

项目不会从 `.env` 文件自动读取配置。`.env.example` 仅用于列出所需变量；请在启动应用的同一个 PowerShell 会话中设置它们。

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/tlias?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的数据库密码"

$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD=""
$env:REDIS_DATABASE="0"
$env:REDIS_TIMEOUT="2s"

# 生成一次安全的 Base64 JWT 密钥，也可以使用你自己的密钥管理方案
$jwtBytes = [byte[]](1..32 | ForEach-Object { Get-Random -Maximum 256 })
$env:JWT_SIGN_KEY = [Convert]::ToBase64String($jwtBytes)
$env:JWT_EXPIRE_MS="43200000"

$env:OSS_ENDPOINT="https://oss-cn-beijing.aliyuncs.com"
$env:OSS_BUCKET_NAME="你的Bucket名称"
$env:OSS_REGION="cn-beijing"
$env:OSS_ACCESS_KEY_ID="你的AccessKey ID"
$env:OSS_ACCESS_KEY_SECRET="你的AccessKey Secret"
```

不要把真实密码、JWT 密钥或阿里云访问凭据写入代码、README 或提交到 Git。

## 启动项目

只运行后端时：

1. 启动 MySQL。
2. 启动 Redis。
3. 设置上述环境变量。
4. 在项目根目录启动后端：

```powershell
mvn spring-boot:run
```

运行完整前后端时，当前本地环境的启动顺序为：

1. MySQL
2. Redis
3. Nginx
4. Spring Boot 后端
5. 前端项目

## Redis 缓存

启动应用前，请确保配置的 Redis 可访问。安装了 Redis CLI 时可以执行：

```powershell
redis-cli -h localhost -p 6379 ping
```

返回 `PONG` 表示连接成功。本项目缓存以下数据：

| 缓存 | 内容 | TTL |
|---|---|---:|
| `deptList` | 部门列表 | 10 分钟 |
| `clazzList` | 班级下拉列表 | 5 分钟 |
| `empDetail` | 员工详情 | 5 分钟 |
| `report` | 员工与学生统计报表 | 2 分钟 |

缓存键统一使用 `tlias:<cacheName>::<key>` 前缀。新增、修改或删除相关数据后会清理对应缓存；事务方法在数据库提交后才执行失效操作。

Redis 不可用时，缓存异常会被记录为 WARN 日志，查询继续回退 MySQL。此时系统可以工作，但不会获得缓存收益。分页查询参数组合较多，目前不进入缓存。

详细设计和验证步骤见 `docs/cache-design.md`。

## 运行测试

```powershell
mvn clean test
```

测试配置使用占位凭据，不会访问阿里云 OSS，也不会写入真实数据库。

## 主要接口模块

- `/login`：用户登录
- `/depts`：部门管理
- `/emps`：员工及工作经历管理
- `/clazzs`：班级管理
- `/students`：学生管理
- `/report`：统计报表
- `/upload`：文件上传
- `/log`：操作日志查询

除登录接口外，其他接口需要在请求头中携带 JWT。当前前端使用 `token` 请求头，后续计划升级为标准的 `Authorization: Bearer <token>` 格式。
