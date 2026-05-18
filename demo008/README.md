# demo008 - Spring Boot + MyBatis（最终现代化版本）

> **教学定位**：**集大成之作** —— 把前面所有知识点用最现代的方式重新实现一遍。

---

## 本 Demo 的里程碑意义

从 demo001 到 demo007，我们一步步走了过来：

- 手写 JDBC
- 分层架构
- Lombok 简化
- 加入日志
- MyBatis XML → 注解
- MyBatis-Spring 手动配置

**demo008** 是最终站：

我们把 `applicationContext.xml` 全部扔掉，换成 **Spring Boot 自动配置**。

---

## 本次最大变化

| 项目                    | demo007（传统 Spring）             | demo008（Spring Boot）                     |
|-------------------------|------------------------------------|--------------------------------------------|
| 配置文件                | applicationContext.xml             | **application.yml**（极简）                |
| 启动方式                | ClassPathXmlApplicationContext     | `@SpringBootApplication` + 自动装配        |
| MyBatis 集成            | 手动配置 SqlSessionFactoryBean     | `mybatis-spring-boot-starter` 自动完成     |
| Mapper 扫描             | MapperScannerConfigurer            | 自动扫描（默认规则）                       |
| 代码复杂度              | 中等                               | **最低**                                   |
| 实际项目推荐度          | 低                                 | **极高**                                   |

---

## 核心配置文件：application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://...
    username: root
    password: 123456

mybatis:
  type-aliases-package: com.demo008.student.entity
  configuration:
    map-underscore-to-camel-case: true
```

Spring Boot + mybatis-spring-boot-starter 会自动：
- 创建 DataSource
- 创建 SqlSessionFactory
- 扫描 Mapper 接口并注入代理

几乎零配置！

---

## 运行方式（Spring Boot 风格）

```bash
# 方式一：Maven 插件
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package
java -jar target/student-springboot-mybatis-1.0-SNAPSHOT.jar
```

---

## 代码极简展示

**Main.java**

```java
@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private StudentController controller;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        controller.start();
    }
}
```

Service 和 Controller 只需要 `@Service` + `@Autowired` 即可。

---

## 学习建议

1. 把 demo007 和 demo008 的 `pom.xml`、`Main.java` 以及配置文件做对比
2. 删除 `application.yml` 里的 `logging.level` 配置，观察控制台输出变化
3. 思考：真实项目中，你会选择 demo007 还是 demo008 的写法？

---

## 系列总结

到 demo008 为止，你已经完整掌握了：

- JDBC 底层原理
- 三层架构思想
- Lombok 现代用法
- MyBatis 两种写法（XML + 注解）
- Spring 依赖注入
- Spring Boot 自动配置

**恭喜！你已经具备了进入真实企业级 Java 后端开发的能力。**

---

需要我继续扩展这个系列吗？（例如加上事务、统一返回结果、RESTful API、MyBatis-Plus 等）

随时告诉我你的想法！