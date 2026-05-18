# demo005 - MyBatis XML 方式替换 Mapper 层（第一步）

> **教学定位**：**只替换 Mapper 层**，其他所有代码尽量不动。

---

## 本 Demo 的唯一目的

**把原来写满 JDBC 代码的 `StudentMapperImpl.java` 彻底干掉，换成 MyBatis + XML。**

这是整个系列第一次引入 **ORM**（对象关系映射）。

---

## 和 demo004 的核心区别

| 对比项                  | demo004（手写 JDBC）                  | demo005（MyBatis）                          |
|-------------------------|---------------------------------------|---------------------------------------------|
| StudentMapperImpl.java  | 存在，里面有大量 JDBC 代码            | **已删除**                                  |
| SQL 写在哪里？          | 写在 Java 代码里                      | 写在 `StudentMapper.xml` 里                 |
| 结果映射                | 手动 `rs.getString("name")`           | MyBatis 自动映射到 Entity                   |
| StudentMapper 接口      | 需要自己写实现类                      | MyBatis 动态代理实现类                      |
| Service 层              | 几乎不变                              | 只改了获取 Mapper 的方式                    |
| Controller + Entity     | 完全没变                              | 完全没变                                    |

**上层几乎无感知** —— 这就是分层 + MyBatis 的威力。

---

## 新增/修改的文件

```
src/main/resources/
├── mybatis-config.xml          ← MyBatis 全局配置
└── mapper/
    └── StudentMapper.xml       ← 最重要的文件！所有 SQL 在这里

src/main/java/.../util/
└── MyBatisUtil.java            ← 新工具类，负责初始化 MyBatis

src/main/java/.../mapper/
└── StudentMapper.java          ← 接口（已无实现类）

src/main/java/.../service/impl/
└── StudentServiceImpl.java     ← 获取 Mapper 的方式变了
```

---

## 重点教学内容

### 1. StudentMapper.xml（最核心）

```xml
<select id="selectAll" resultType="com.demo005.student.entity.Student">
    SELECT ... FROM t_student
</select>
```

- `id` 必须和接口方法名一致
- `resultType` 告诉 MyBatis 把结果自动转成 Student 对象
- `#{name}` 占位符，MyBatis 会自动预编译

### 2. MyBatisUtil

我们手动创建了 `SqlSessionFactory`，这是目前最原始的 MyBatis 用法。

后面当我们引入 Spring 后，这段代码会大幅简化。

### 3. ServiceImpl 中的变化

```java
try (SqlSession session = MyBatisUtil.getSqlSession()) {
    StudentMapper mapper = session.getMapper(StudentMapper.class);
    return mapper.selectAll();
}
```

`getMapper()` 会返回一个**动态代理对象**，MyBatis 会在运行时帮我们实现接口。

---

## 如何运行

```bash
cd demo005
mvn clean compile exec:java
```

---

## 强烈建议做的对比

1. 把 `demo004` 和 `demo005` 的 `StudentServiceImpl.java` 打开对比
2. 把 `demo004` 里曾经存在的 `StudentMapperImpl.java`（你可以用 git 找回） 和现在的 `StudentMapper.xml` 对比

你会深刻感受到：**同样是做 CRUD，代码量和复杂度下降了多少**。

---

## 下一步（demo006）

我们会把 `StudentMapper.xml` 里的 SQL 改成**注解方式**（`@Select`、`@Insert` 等），进一步简化。

请继续保持这个极慢的节奏。

---

**准备好了吗？** 运行一下 demo005，感受 MyBatis 第一次带给你的震撼吧！
