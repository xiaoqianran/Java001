# demo002 - 传统分层架构（无 Lombok）

> **教学定位**：理解「分层」到底解决了 demo001 的什么问题。

## 和 demo001 的核心区别

| 维度         | demo001（原始版）               | demo002（分层版）                     |
|--------------|----------------------------------|---------------------------------------|
| 文件数量     | 1 个 Java 文件                   | 8 个 Java 文件                        |
| 代码行数     | ~250 行                          | ~400 行（但每个文件都很小）           |
| 结构         | 全部代码写死在一起               | Entity + Mapper + Service + Controller |
| 可维护性     | 极差                             | 好很多                                |
| 能否复用     | 几乎不可能                       | Service 可以被多个 Controller 调用    |
| 切换数据库   | 几乎要重写整个类                 | 只改 MapperImpl 即可                  |

## 分层架构图（本 Demo 真实结构）

```
用户（控制台输入）
        ↓
StudentController（接收输入、展示结果）
        ↓
StudentService（业务规则、事务）
        ↓
StudentMapper（SQL + 结果映射）
        ↓
MySQL 数据库
```

每一层只做「自己该做的事」：
- **Controller**：只和「人」打交道
- **Service**：放业务逻辑
- **Mapper**：只和数据库打交道
- **Entity**：纯数据载体

## 代码结构

```
src/main/java/com/demo002/student/
├── Main.java                          ← 启动入口（只有3行）
├── controller/
│   └── StudentController.java         ← 菜单交互
├── service/
│   ├── StudentService.java            ← 接口
│   └── impl/
│       └── StudentServiceImpl.java    ← 实现（手动 new Mapper）
├── mapper/
│   ├── StudentMapper.java             ← 接口（定义了增删改查方法）
│   └── impl/
│       └── StudentMapperImpl.java     ← 真正的 JDBC 代码在这里
├── entity/
│   └── Student.java                   ← 手写 80+ 行 getter/setter
└── util/
    └── DBUtil.java                    ← 数据库连接工具
```

## 重点教学内容

### 1. 为什么要有「接口 + 实现」？

看 `StudentMapper` 接口和 `StudentMapperImpl`：

```java
// Service 层只知道有这个接口
private final StudentMapper studentMapper = new StudentMapperImpl();
```

**好处**：
- 以后想换成 MyBatis 实现，只需要写一个 `MyBatisStudentMapper` 实现同一个接口
- Service 层的代码一行都不用改！

### 2. 「手动装配」是什么鬼？

在 `StudentServiceImpl` 里我们写了这行：

```java
private final StudentMapper studentMapper = new StudentMapperImpl();
```

这叫做**手动依赖注入**。

等你学了 Spring 框架后，这一行会变成：

```java
@Autowired
private StudentMapper studentMapper;   // 或者构造器注入
```

Spring 会帮你 new 对象并注入进来。

### 3. Entity 为什么这么长？

`Student.java` 里我们手写了：
- 8 个字段
- 2 个构造方法
- 16 个 getter/setter 方法
- 1 个 toString

**一共 80+ 行代码！**

这就是为什么 Lombok 这么受欢迎 —— demo003 会让你大吃一惊。

## 本版本仍然存在的问题（为 demo003 做铺垫）

1. **Entity 太胖** —— 80% 代码都是 getter/setter
2. **重复的 try-with-resources** —— 每个 Mapper 方法都要写
3. **还没有真正的事务**（多条 SQL 无法一起回滚）
4. **Controller 里 new Service** 还是手动

## 如何运行

```bash
cd demo002
mvn clean compile exec:java
```

## 学习任务（非常推荐做）

1. 在 `StudentServiceImpl.addStudent` 方法里增加「手机号格式校验」
2. 尝试把 `updateStudent` 的参数接收逻辑也补全（目前我只写了部分字段）
3. 思考：如果现在要支持「班级」功能，需要新建哪些文件？

---

完成本 Demo 后，请进入 **[demo003](../demo003/README.md)**，我们会用 Lombok 把代码量砍掉一半以上！
