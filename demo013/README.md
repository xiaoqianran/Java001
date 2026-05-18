# demo013 - 参数校验（Validation）

**核心目标**：让接口参数校验规范化、自动化。

## 主要改进

- 新增 `StudentCreateDTO` / `StudentUpdateDTO`
- 使用 `@NotBlank`、`@Email`、`@Min` 等注解
- Controller 方法使用 `@Valid`
- `GlobalExceptionHandler` 增强处理 `MethodArgumentNotValidException`

## 测试示例

```bash
# 故意传错参数
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{"name":"","age":0,"gender":3}'
```

返回：
```json
{
  "success": false,
  "code": 400,
  "message": "name: 姓名不能为空, age: 年龄最小为1岁, gender: 性别只能是0或1"
}
```

非常优雅！

## 运行

```bash
cd demo013
mvn spring-boot:run
```

---

**下一步**：demo014 分页 + 条件查询（非常实用）。

继续吗？直接回复“继续”。