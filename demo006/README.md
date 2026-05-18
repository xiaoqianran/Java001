# demo006 - MyBatis 注解方式（XML → Annotation）

> **教学定位**：**只把 SQL 的写法从 XML 改成注解**，其他全部不变。

---

## 本 Demo 唯一做的事

在 **demo005（MyBatis XML）** 的基础上，把 `StudentMapper.xml` 里的所有 SQL 迁移到 `StudentMapper.java` 接口的注解上。

---

## 核心对比：XML vs Annotation

### demo005（XML 方式）

```xml
<!-- StudentMapper.xml -->
<select id="selectAll" resultType="...">
    SELECT * FROM t_student
</select>
```

### demo006（注解方式）

```java
@Select("SELECT * FROM t_student")
List<Student> selectAll();
```

---

## 优缺点对比（教学重点）

| 维度           | XML 方式（demo005）               | 注解方式（demo006）                     | 推荐场景          |
|----------------|-----------------------------------|-----------------------------------------|-------------------|
| SQL 可读性     | 很好                              | 一般（长 SQL 会很难看）                 | 小项目            |
| 动态 SQL       | 非常强（if/choose/foreach）       | 较弱（需要 @Provider 或 XML）           | 复杂查询          |
| 文件数量       | 多（每个 Mapper 一个 XML）        | 少（全在接口里）                        | 追求简洁          |
| 重构难度       | 较难（需要同时改接口和 XML）      | 简单（改一个文件）                      | 快速开发          |
| 团队协作       | SQL 独立文件，便于 DBA 审核       | 开发人员全栈负责                        | 小团队            |

**真实项目中的做法**：
- 简单 CRUD → 常用注解
- 复杂查询 + 动态条件 → 仍然使用 XML 或 MyBatis-Plus

---

## 本次修改的文件

1. `StudentMapper.java` — 增加了 `@Select`、`@Insert`、`@Update`、`@Delete`
2. `mybatis-config.xml` — 把 `<mapper resource>` 改成 `<mapper class>`
3. 删除了 `StudentMapper.xml`
4. ServiceImpl 注释更新（强调变化很小）

---

## 运行

```bash
cd demo006
mvn clean compile exec:java
```

---

## 学习建议

1. 把 demo005 和 demo006 的 `StudentMapper.java` 放在一起对比
2. 思考：如果你的项目有大量动态 SQL，你会选择哪种写法？
3. 感受“上层代码几乎零改动”带来的好处

---

## 下一步（demo007）

我们会引入 **MyBatis-Spring**，彻底解决当前最痛苦的问题：

- 每次方法都要 `try-with-resources` 开 SqlSession
- 手动管理事务

敬请期待！
