# JWT 持久化会话设计

## 目标

原来的 JWT 只校验签名和过期时间。一旦令牌签发，在过期前服务端无法主动让它失效。
本方案使用 Redis 保存活跃会话，使系统具备退出即失效和服务端撤销能力，同时保留 JWT
无须查询 MySQL 的特点。

## 登录与鉴权流程

登录成功时：

1. 查询并验证用户名、密码；
2. 生成包含员工信息、`jti`、签发时间和过期时间的 JWT；
3. 计算 JWT 的 SHA-256 摘要；
4. 将摘要写入 `tlias:auth:session:<jti>`，TTL 等于 JWT 剩余有效期；
5. Redis 写入成功后才向客户端返回令牌。

访问受保护接口时：

1. 从 `token` 请求头读取 JWT；
2. 校验签名与过期时间；
3. 按 `jti` 查询 Redis；
4. 使用常量时间比较校验令牌摘要；
5. 校验成功后将员工 ID 放入 `CurrentHolder` 并放行。

退出时删除当前 `jti` 的 Redis 键。JWT 的 TTL 到期后，Redis 也会自动清理会话，不需要定时任务。

## 安全取舍

- Redis 只保存不可逆摘要，不保存可直接重放的原始令牌。
- 每次登录生成独立 `jti`，支持同一账号多端同时在线。
- 会话校验采用 fail-closed：Redis 不可用时返回 503，而不是跳过撤销检查。
- 业务缓存仍可在 Redis 故障时降级到 MySQL；两类数据的故障策略不同。
- 当前实现是会话白名单。若将来请求量很大，可再评估短期 access token、refresh token 与撤销列表方案。

## 手工验证

先确保 MySQL 和 Redis 已启动，并按 README 设置环境变量，然后启动后端。

```powershell
$loginBody = @{ username = "你的用户名"; password = "你的密码" } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/login" `
  -ContentType "application/json" -Body $loginBody
$token = $login.data.token

redis-cli --scan --pattern "tlias:auth:session:*"

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/logout" `
  -Headers @{ token = $token }
```

退出后再次携带原令牌访问任一受保护接口，应返回 HTTP 401。停止 Redis 后请求受保护接口，
应返回 HTTP 503；恢复 Redis 后，尚未过期的已有会话仍可继续使用。
