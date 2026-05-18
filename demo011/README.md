# demo011 - 统一返回结果 Result + 全局异常处理

> **教学定位**：向生产级项目迈进的重要一步 —— 统一响应格式 + 异常集中处理。

---

## 本 Demo 的核心内容

1. **Result<T>** - 统一响应结果封装
2. **BusinessException** - 自定义业务异常
3. **GlobalExceptionHandler** - 全局异常处理器

---

## Result<T> 的标准使用

```java
// 成功
return Result.success(data);
return Result.success("新增成功", data);

// 失败
return Result.error("操作失败");
return Result.error(400, "参数非法");
```

---

## 全局异常处理的价值

以前：
- 异常直接抛出，控制台/前端看到堆栈
- 每个方法都要 try-catch

现在：
- 所有异常被 GlobalExceptionHandler 统一捕获
- 自动包装成 Result 格式返回
- 代码更干净，维护性更强

---

## 运行

```bash
cd demo011
mvn spring-boot:run
```

---

## 学习重点

- 对比 demo010 和 demo011 的 ServiceImpl（异常抛出方式）
- 查看 GlobalExceptionHandler 如何工作
- 理解为什么生产项目必须有统一 Result

---

## 下一步

demo012 可以加入 `@Transactional` 事务管理 + 自定义事务传播行为。

继续吗？直接说“继续”即可。