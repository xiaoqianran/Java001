# demo015 - 事务管理（@Transactional）

**教学目标**：理解 Spring 事务，如何保证数据一致性。

## 主要变化

- 在 `StudentServiceImpl` 中新增 `batchAddStudents` 方法
- 使用 `@Transactional(rollbackFor = Exception.class)`
- Controller 新增 `POST /students/batch` 接口
- 通过名字包含 "rollback" 来模拟业务异常，触发回滚

## 测试事务回滚

```bash
curl -X POST http://localhost:8080/students/batch \
  -H "Content-Type: application/json" \
  -d '[
    {"name":"正常学生1","age":20,"gender":1},
    {"name":"rollback测试","age":21,"gender":0}
  ]'
```

返回错误后，数据库中 "正常学生1" 应该**没有被插入**（因为事务回滚）。

## 关键知识点

- `@Transactional` 默认只对 `RuntimeException` 和 `Error` 回滚
- `rollbackFor = Exception.class` 可以让受检异常也回滚
- 事务传播行为、隔离级别等（进阶）

## 运行

```bash
cd demo015
mvn spring-boot:run
```

---

**系列到此（demo001~demo015）已经非常完整**，涵盖了从 JDBC 到现代 Spring Boot + MyBatis-Plus + 事务 + 校验 + REST API 的完整演进路径。

需要继续吗？（例如统一日志、Redis缓存、Docker部署等）

直接告诉我！