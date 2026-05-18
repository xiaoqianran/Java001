# demo007 - MyBatis-Spring 集成（革命性进步）

> **教学定位**：**彻底告别手动管理 SqlSession**，进入真正的 Spring 时代。

---

## 本 Demo 的核心意义

这是整个教学系列目前为止**最重要的一次升级**。

从 demo001 到 demo006，我们一直在忍受一件非常痛苦的事：

```java
// 以前每次都要写这个
try (SqlSession session = MyBatisUtil.getSqlSession()) {
    StudentMapper mapper = session.getMapper(StudentMapper.class);
    // 业务代码...
}
```

**demo007 之后，这段代码彻底消失了！**

---

## 本次主要变化

| 变化点                    | demo006（之前）                     | demo007（现在）                        |
|---------------------------|-------------------------------------|----------------------------------------|
| Service 获取 Mapper       | 手动 `try + getMapper`              | `@Autowired` 直接注入                  |
| SqlSession 管理           | 每次方法手动开关                    | Spring 完全托管                        |
| 对象创建                  | `new StudentServiceImpl()`          | Spring 容器管理 + 依赖注入             |
| 启动方式                  | 直接 new Controller                 | `ApplicationContext` 启动              |
| 配置文件                  | 只有 mybatis-config.xml             | 新增 `applicationContext.xml`          |

---

## 核心配置文件解析

### applicationContext.xml

```xml
<!-- 数据源 -->
<bean id="dataSource" ... />

<!-- MyBatis 的 SqlSessionFactory -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
</bean>

<!-- 神器：MapperScannerConfigurer -->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.demo007.student.mapper"/>
</bean>
```

`MapperScannerConfigurer` 会扫描 `mapper` 包下所有接口，自动为它们创建代理对象并注册到 Spring 容器中。

---

## ServiceImpl 变得极简

```java
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;   // 直接用！什么都不用管

    public boolean addStudent(Student student) {
        return studentMapper.insert(student) > 0;
    }
}
```

---

## 运行

```bash
cd demo007
mvn clean compile exec:java
```

---

## 强烈建议的对比

1. 对比 demo006 和 demo007 的 `StudentServiceImpl.java`
2. 对比 `Main.java` 的启动方式
3. 感受“代码量大幅下降 + 可维护性大幅上升”

---

## 下一步（demo008）

我们会升级到 **Spring Boot**，把 `applicationContext.xml` 扔掉，改用 `application.yml` + 自动配置。

届时整个项目会变得更加现代和简洁。

---

**恭喜你！** 你已经走完了从最原始 JDBC → 现代 Spring + MyBatis 的最关键一步。

继续保持这个节奏，下一版见！
