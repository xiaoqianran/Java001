# demo004 - 在 Lombok 基础上增加日志（@Slf4j）

> **本版本教学定位**：**严格在 demo003 的基础上，只增加日志功能**。

---

## 严格遵循的教学节奏（重要）

根据你的要求，我们的演进路径是：

1. **demo002** → 完整分层（无 Lombok，全手写 getter/setter）
2. **demo003** → 在 demo002 基础上 **只引入 Lombok**（只改 Entity.java）
3. **demo004** → 在 **demo003（已带 Lombok）** 的基础上 **只增加日志**（@Slf4j）

**每一步只做一件事**，让学习者能清晰感受到「这一步到底改了什么」。

---

## demo004 到底改了什么？

**只做了两件事**：

1. 在 `StudentServiceImpl` 和 `StudentController` 中加入 `@Slf4j` 注解
2. 把原来分散的 `System.out.println` 和 `System.out.printf` 逐步替换成 `log.info()`、`log.warn()`、`log.debug()`

除此之外：
- Entity 层没有任何新变化（继续使用 demo003 的 Lombok 版本）
- Mapper 层完全没动
- 业务流程、菜单交互方式基本保持一致

---

## Lombok 在本版本的新用法

### 1. @Slf4j（最主要的新内容）

```java
@Slf4j
public class StudentServiceImpl implements StudentService {

    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());
        log.warn("姓名为空，新增失败");
        ...
    }
}
```

**@Slf4j 的作用**：
- Lombok 会在编译期自动帮你生成下面这行代码：
  ```java
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StudentServiceImpl.class);
  ```
- 你可以直接使用 `log.info()`、`log.debug()`、`log.warn()`、`log.error()` 等方法。

### 2. 为什么不在 demo003 就加日志？

因为我们要**一步一步来**：
- 先让大家接受 Lombok（demo003）
- 再基于 Lombok 去接受日志框架（demo004）

这样学习负担最小。

---

## 依赖变化

为了让 `@Slf4j` 能真正工作，我们在 `pom.xml` 中新增了：

```xml
<!-- SLF4J API + 简单实现 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.13</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.13</version>
</dependency>
```

`slf4j-simple` 是一个非常轻量的日志实现，适合教学阶段使用。后面真实项目通常会换成 Logback 或 Log4j2。

---

## 运行本版本

```bash
cd demo004
mvn clean compile exec:java
```

启动后，你会发现控制台除了业务输出，还会带上类似下面的日志格式：

```
[main] INFO com.demo004.student.service.impl.StudentServiceImpl - 准备新增学生: 张三
```

---

## 学习重点（请对比 demo003）

- 打开 demo003 和 demo004 的 `StudentServiceImpl.java` 做对比
- 感受 `@Slf4j` 带来了什么变化
- 思考：如果以后要换成 Logback，该怎么改？（答案：几乎不用改业务代码，只改 pom 和配置文件）

---

## 下一步计划（已和你确认的节奏）

接下来我们会进入 **MyBatis** 阶段（demo005 开始）。

同样会采用**非常缓慢**的节奏：

- demo005 第一版：先只把 `StudentMapper` 替换成 MyBatis（XML 方式），其他层尽量不动
- 后续版本再逐步引入注解、MapperScan、事务、Spring Boot 等

请告诉我：

1. demo004 当前的「只加日志」定位是否符合你的预期？
2. demo005 的 MyBatis 引入，你希望第一步做到什么程度？（例如：纯 MyBatis + 手写 SqlSessionFactory，还是直接上 MyBatis-Spring？）

随时指挥，我会严格按照「每一步只做一小步」的原则继续推进。
