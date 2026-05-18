# demo010 - MyBatis-Plus Service 层封装（再一次简化）

> **教学定位**：**Service 层也极致简化** —— 体验 MyBatis-Plus 的完整威力。

---

## 本 Demo 的核心进步

在 **demo009** 中，我们已经通过 `BaseMapper` 让 Mapper 层几乎消失。

**demo010** 更进一步：让 **Service 层** 也大幅瘦身。

---

## 关键变化对比

### 1. StudentService 接口

**demo009**：
```java
public interface StudentService {
    boolean addStudent(Student student);
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    boolean updateStudent(Student student);
    boolean deleteStudent(Long id);
}
```

**demo010**：
```java
public interface StudentService extends IService<Student> {
    boolean addStudent(Student student);
    // 其他方法可以完全继承自 IService
}
```

### 2. StudentServiceImpl（最大变化）

**demo009**（手动调用 mapper）：
```java
public boolean addStudent(Student student) { ... }
public List<Student> getAllStudents() { return studentMapper.selectList(null); }
public Student getStudentById(Long id) { return studentMapper.selectById(id); }
...
```

**demo010**（继承 ServiceImpl）：
```java
public class StudentServiceImpl 
        extends ServiceImpl<StudentMapper, Student> 
        implements StudentService {

    @Override
    public boolean addStudent(Student student) {
        // 业务校验...
        return this.save(student);   // 直接用父类方法
    }

    @Override
    public List<Student> getAllStudents() {
        return this.list();          // 直接用父类方法
    }
    ...
}
```

**继承 `ServiceImpl` 后自动获得的能力**：
- `save()`、`saveBatch()`
- `getById()`、`list()`、`listByIds()`
- `updateById()`、`updateBatchById()`
- `removeById()`、`removeBatchByIds()`
- `page()` 分页查询
- 等等...

---

## 带来的巨大好处

1. **代码量再次腰斩**
2. **一致性**：所有项目 Service 层的 CRUD 风格统一
3. **生产力**：新功能开发速度大幅提升
4. **可维护性**：框架帮你处理了很多边界情况

---

## 运行

```bash
cd demo010
mvn spring-boot:run
```

---

## 学习建议

1. 把 **demo009** 和 **demo010** 的 `StudentService.java` + `StudentServiceImpl.java` 放在一起做**最直观对比**
2. 思考：什么时候应该自己写实现，什么时候应该直接继承 `ServiceImpl`？
3. 感受“**让框架做框架的事，让业务做业务的事**”的哲学

---

## 系列到此的完整进化路径

- demo001：手写 200+ 行 JDBC
- ...
- demo008：Spring Boot + 普通 MyBatis
- demo009：MyBatis-Plus BaseMapper（Mapper 层解放）
- **demo010**：MyBatis-Plus ServiceImpl（Service 层也解放）

**到目前为止，你已经掌握了目前国内主流 Java 后端最推荐的技术栈写法！**

---

需要继续吗？可以考虑的方向：

- demo011：统一返回结果 + 全局异常处理
- demo012：加入事务 @Transactional
- demo013：RESTful API 改造（从控制台变成真正 Web 项目）
- 或者暂停，给你留时间消化

请告诉我你的想法。