# demo003 - 传统分层 + Lombok（最小改动版）

> **教学定位**：演示「只改一个文件」就能获得巨大收益。

## 本 Demo 的设计意图

用户反馈「003只给了一个学生文件」—— **这正是我故意为之**！

demo003 的核心教学价值在于：

**我们只改了 `Student.java` 这一个文件，就把 Entity 层的代码从 85 行减少到 22 行。**

其他 7 个文件（Controller、Service、MapperImpl 等）**和 demo002 完全一样**，一行都没动。

这才是真实项目中引入 Lombok 的正确方式 —— **渐进式、低风险**。

## 唯一的变化文件

```
demo003/src/main/java/com/demo003/student/entity/Student.java
```

使用了：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student { ... }
```

## 为什么 demo003 故意「只改一个文件」？

1. 降低学习曲线：你能清楚看到「前后对比」
2. 证明 Lombok 的低侵入性：不需要重构整个项目
3. 为 demo004 做铺垫：demo004 会在这个基础上「全面拥抱 Lombok」

## 编译 & 运行

```bash
cd demo003
mvn clean compile exec:java
```

记得先执行一次 `sql/init.sql`。

## 接下来（demo004）

在 demo004 中，我们会：

- 全面使用 Lombok（不只是 Entity）
- 使用 `@Slf4j` 写日志
- 使用 `@RequiredArgsConstructor` 实现更优雅的「依赖注入」写法（模拟 Spring 的构造器注入）
- 可能引入简单的 DTO 类

这才是「用 Lombok 重新实现 002 的完整分层过程」。

---

准备好进入下一个阶段了吗？进入 [demo004](../demo004/README.md)
