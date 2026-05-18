# demo012 - RESTful API 改造（从控制台到 Web）

> **教学定位**：将之前的控制台程序彻底改造为现代 REST API。

---

## 本 Demo 的重大变化

- 移除了所有控制台菜单代码（Scanner、start() 方法）
- 新增 `@RestController` + `@RequestMapping` 风格的接口
- 所有接口统一返回 `Result<T>`
- 全局异常处理器继续生效，异常也会返回标准 JSON

---

## REST 接口列表

| 方法   | 路径              | 说明         | 请求体          |
|--------|-------------------|--------------|-----------------|
| GET    | /students         | 查询所有     | -               |
| GET    | /students/{id}    | 根据ID查询   | -               |
| POST   | /students         | 新增学生     | Student JSON    |
| PUT    | /students/{id}    | 修改学生     | Student JSON    |
| DELETE | /students/{id}    | 删除学生     | -               |

---

## 测试示例（使用 curl 或 Postman）

```bash
# 查询所有
curl http://localhost:8080/students

# 新增
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","age":20,"gender":1}'

# 根据ID查询
curl http://localhost:8080/students/1
```

---

## 运行

```bash
cd demo012
mvn spring-boot:run
```

启动后访问：http://localhost:8080/students

---

## 学习重点

- 理解 RESTful 设计原则
- `@RestController` vs `@Controller` 的区别
- `@RequestBody` 和 `@PathVariable` 的使用
- 为什么统一返回 Result 很重要

---

## 下一步

可以继续完善：
- 加入参数校验（@Valid + Validation）
- 分页查询
- 统一日志 + 请求响应日志
- JWT 认证等

继续吗？直接说“继续”即可。