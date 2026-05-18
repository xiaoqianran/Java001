# demo009 - MyBatis-Plus（极致简化）

> **教学定位**：**再一次大幅简化** —— 看看 MyBatis-Plus 还能帮我们省掉多少代码。

---

## 本 Demo 的震撼点

**从 demo008 到 demo009，我们只改了三件事，就让代码量再次腰斩：**

1. `StudentMapper` 从写 5 个方法 → 变成空接口（只继承 `BaseMapper`）
2. `StudentServiceImpl` 中的 CRUD 全部换成 MyBatis-Plus 标准方法
3. Entity 增加了少量 MyBatis-Plus 注解

---

## 代码简化对比（最直观）

### demo008 的 StudentMapper

```java
public interface StudentMapper {
    int insert(Student student);
    Student selectById(Long id);
    List<Student> selectAll();
    int update(Student student);
    int deleteById(Long id);
    // 还要写对应的 @Insert / @Select 注解
}
```

### demo009 的 StudentMapper

```java
public interface StudentMapper extends BaseMapper<Student> {
    // 空的！什么都不用写
}
```

**BaseMapper 自动提供了**：
- `insert(entity)`
- `deleteById(id)`
- `updateById(entity)`
- `selectById(id)`
- `selectList(null)` （查询所有）
- `selectPage(page, queryWrapper)` 等分页方法
- 共 20+ 个常用方法

---

## ServiceImpl 的变化

**demo008** 里我们还在写：
```java
studentMapper.selectAll();
studentMapper.deleteById(id);
```

**demo009** 里直接用：
```java
studentMapper.selectList(null);
studentMapper.deleteById(id);
```

而且 MyBatis-Plus 还提供了更高级的 `IService` + `ServiceImpl`，如果我们想继续简化，Service 层也可以再省一大半代码（后面可以再出一版）。

---

## 运行

```bash
cd demo009
mvn spring-boot:run
```

---

## 学习重点

1. 打开 demo008 和 demo009 的 `StudentMapper.java` 做最直观对比
2. 打开 `StudentServiceImpl.java` 对比方法实现
3. 思考：**MyBatis-Plus 到底帮我们省掉了什么？**

答案：
- 省掉了 80% 的基础 CRUD SQL 编写
- 省掉了大量重复的 Mapper 方法
- 提供了很多开箱即用的高级功能（分页、条件构造器、乐观锁等）

---

## 下一步建议

MyBatis-Plus 还能继续简化：

- 让 `StudentServiceImpl` 继承 `ServiceImpl<StudentMapper, Student>` + 实现 `IService<Student>`
- 这样 Service 层也能再省掉很多代码

需要我继续出 **demo010** 吗？（使用 MyBatis-Plus 的 Service 层封装）

---

**到目前为止的系列总结**：

你已经看到了从“手写 200 行 JDBC”到“几乎零 SQL”的完整进化路径。

非常推荐把 demo001 ~ demo009 的关键文件挨个对比一遍，你会收获巨大！