# demo017 - Redis 缓存集成（@Cacheable + 缓存穿透处理）

**教学目标**：掌握 Spring Boot + Redis 缓存的最佳实践，包括声明式缓存注解和缓存穿透/雪崩的防护手段。

## 主要变化

- 引入 `spring-boot-starter-data-redis`
- 新增 `RedisConfig`：自定义 `RedisTemplate`（Jackson 序列化）和 `RedisCacheManager`
- Service 层大量使用 `@Cacheable`、`@CacheEvict`
- 演示两种缓存使用方式：
  1. 声明式注解 `@Cacheable`（最常用）
  2. 手动 RedisTemplate 操作（实现**缓存穿透保护**）
- 新增 `/students/protect/{id}` 接口专门用于测试缓存穿透防护效果

## 核心知识点

- Spring Cache 抽象 + Redis 的集成方式
- `@Cacheable`、`@CacheEvict`、`@CachePut` 的使用与注意事项
- **缓存穿透**：大量查询不存在的数据直接打到 DB → 解决方案：空值缓存 + 短 TTL
- **缓存雪崩**：大量 key 同时过期 → 解决方案：随机 TTL
- Redis 序列化方式的选择（JSON vs 字节）
- 实际项目中的缓存策略设计（key 设计、TTL 策略、null 值处理）

## 运行方式

```bash
# 确保已启动 Docker（MySQL + Redis）
docker compose up -d

cd demo017
mvn spring-boot:run
```

## 缓存效果测试

### 1. 声明式缓存测试（@Cacheable）
```bash
# 第一次查询（会打印 “【未命中缓存】从数据库查询”）
curl http://localhost:8080/students/1

# 第二次及以后查询（直接走 Redis，极快）
curl http://localhost:8080/students/1
```

### 2. 缓存穿透保护测试（重点）
```bash
# 查询一个不存在的学生（第一次打 DB）
curl http://localhost:8080/students/protect/99999

# 再次查询（直接返回空值缓存，不再访问数据库）
curl http://localhost:8080/students/protect/99999
```

观察应用日志，可以清楚看到缓存命中和空值保护的效果。

## 下一步

demo018 将引入 JWT + Spring Security 完成认证体系。

---

**注意**：本阶段仍使用 Student 领域，聚焦基础设施能力建设。完整真实项目将在 demo020 之后启动。