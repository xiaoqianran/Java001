# demo014 - 分页 + 条件查询

**教学目标**：掌握真实项目中最常用的列表查询方式 —— 分页 + 搜索。

## 主要变化

- 在 `StudentService` 中新增 `getStudentPage` 方法
- 使用 MyBatis-Plus 的 `Page` 和 `LambdaQueryWrapper`
- Controller 新增 `/students/page` 接口
- 支持参数：`page`、`size`、`name`（模糊搜索）

## 接口示例

```bash
# 带搜索的分页查询
curl "http://localhost:8080/students/page?page=1&size=5&name=张"
```

返回示例（简化）：

```json
{
  "success": true,
  "code": 200,
  "data": {
    "records": [...],
    "total": 23,
    "size": 5,
    "current": 1,
    "pages": 5
  }
}
```

## 运行

```bash
cd demo014
mvn spring-boot:run
```

---

**下一步**：demo015 - 事务管理。

需要我继续补全 demo015 吗？直接回复“继续”或“补全demo015”。