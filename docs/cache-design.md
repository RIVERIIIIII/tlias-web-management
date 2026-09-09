# Redis 缓存设计

## 目标

通过 Spring Cache 和 Redis 缓存高频、变化相对较少的数据，减少重复查询 MySQL；同时保证写操作成功后及时失效缓存，并在 Redis 故障时允许核心查询回退数据库。

## 缓存对象

| Cache Name | Key | 数据来源 | TTL |
|---|---|---|---:|
| `deptList` | `all` | 部门列表 | 10 分钟 |
| `clazzList` | `all` | 班级下拉列表 | 5 分钟 |
| `empDetail` | 员工 ID | 员工及工作经历详情 | 5 分钟 |
| `report` | `empJob` | 员工职位统计 | 2 分钟 |
| `report` | `empGender` | 员工性别统计 | 2 分钟 |
| `report` | `studentCount` | 班级人数统计 | 2 分钟 |
| `report` | `studentDegree` | 学生学历统计 | 2 分钟 |

Redis 中的物理键使用前缀：

```text
tlias:<cacheName>::<key>
```

## Cache Aside 流程

### 查询

1. Spring Cache 根据缓存名称和 Key 查询 Redis。
2. 命中时直接返回缓存值。
3. 未命中时执行 Service 方法并查询 MySQL。
4. 查询结果经过 JSON 序列化后写入 Redis并设置 TTL。

部门列表、班级列表和报表使用 `sync = true`，同一 JVM 中相同 Key 并发未命中时只允许一个线程加载数据库。

### 更新

1. Service 方法先更新 MySQL。
2. 方法成功后删除对应缓存。
3. 下次查询从 MySQL 重新加载最新数据。

RedisCacheManager 开启 `transactionAware()`。员工新增、删除、更新等事务方法只会在事务成功提交后执行缓存操作，避免数据库回滚但缓存已被清理或更新。

## 失效规则

| 写操作 | 清理缓存 |
|---|---|
| 新增、修改、删除部门 | `deptList` 全部 |
| 新增、修改、删除班级 | `clazzList`、`report` 全部 |
| 新增员工 | `report` 全部 |
| 修改员工 | 对应 `empDetail`、`report` 全部 |
| 批量删除员工 | `empDetail`、`report` 全部 |
| 新增、修改、删除学生或违纪处理 | `report` 全部 |

批量删除员工时选择清空全部员工详情缓存，是因为 Spring Cache 注解无法直接为 ID 集合逐个删除缓存键；相比保留脏数据，清空该小型缓存空间更安全。

## 为什么不缓存分页查询

员工、班级和学生分页接口包含页码、页大小、姓名、性别、日期等组合条件。直接缓存会产生大量低复用 Key，并显著增加失效范围。现阶段只缓存高复用的列表、详情和统计结果。

## 空值策略

当前配置禁止缓存 `null`。这样可以避免长期保存无效数据，但不能完全解决恶意查询不存在 ID 导致的缓存穿透。数据量和访问量提升后，可增加以下方案之一：

- 为不存在的数据设置约 30 秒的专用空值缓存；
- 使用布隆过滤器拦截确定不存在的 ID；
- 对异常高频 Key 增加接口限流。

## 故障降级

`LoggingCacheErrorHandler` 会处理缓存读取、写入、删除和清空异常：

- 记录包含缓存名、Key 和失败原因的 WARN 日志；
- 不把 Redis 异常返回给 Controller；
- 缓存读取失败时继续执行 Service 方法并查询 MySQL。

该策略保证可用性，但 Redis 长期不可用会使所有请求直接访问数据库，因此仍需监控 WARN 日志和数据库负载。

## 序列化

- 缓存值使用带类型信息的 JSON，而不是 JDK 二进制序列化。
- ObjectMapper 保留 Spring Boot 的 Java Time 支持。
- 反序列化类型限制在 `com.itheima.pojo`、`java.time` 和 `java.util` 范围内。
- 已通过部门列表及 `LocalDateTime` 往返序列化测试。

## 自动化验证

当前测试覆盖：

- 相同部门列表查询两次，Mapper 只执行一次；
- 修改部门后列表缓存失效，下一次查询重新访问 Mapper；
- 缓存读写异常不会替代业务结果；
- POJO 列表和 Java Time 可以完成 JSON 序列化与反序列化；
- Spring Boot 上下文可以在不连接 Redis 的情况下启动。

运行：

```powershell
mvn clean test
```

## 真实 Redis 验证

当前开发机尚未安装 Docker 或本地 Redis，因此自动化测试不包含真实 Redis 进程。安装 Redis 后按以下步骤验证：

1. 启动 Redis，并使用 `redis-cli ping` 确认返回 `PONG`。
2. 设置 README 中的 Redis 环境变量并启动后端。
3. 使用有效 JWT 连续请求两次 `/depts`。
4. 观察 MyBatis SQL 日志：第一次应查询数据库，第二次不应重复查询。
5. 查询缓存键：

```powershell
redis-cli --scan --pattern "tlias:*"
```

6. 调用部门修改接口后再次请求 `/depts`，应重新执行一次数据库查询并生成新缓存。
