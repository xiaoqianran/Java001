# demo001 - 最原始的 JDBC 学生管理系统

> **教学定位**：让你第一次「看清楚」Java 操作数据库的底层过程。

## 本 Demo 要解决的核心问题

很多小白第一次学 JDBC 就被「一大堆概念」搞蒙了：
- Connection 是什么？
- PreparedStatement 和 Statement 有什么区别？
- ResultSet 怎么取数据？
- 为什么要 finally 关闭资源？

**这个 Demo 的唯一目的就是把这些东西用最直白的方式写出来。**

## 代码结构（极简）

```
demo001/
├── pom.xml
├── sql/init.sql
└── src/main/java/com/demo001/student/
    └── StudentJdbcDemo.java     ← 所有代码都在这一个文件！
```

是的，整个系统只有一个 Java 类。

## 快速开始

### 1. 确保 MySQL 已启动并执行过建表脚本

```bash
# 在项目根目录执行（或者直接复制 sql/init.sql 内容到 MySQL 执行）
docker exec -i mysql-student mysql -uroot -p123456 < demo001/sql/init.sql
```

### 2. 运行程序

```bash
cd demo001
mvn clean compile exec:java
```

第一次运行会自动下载 MySQL 驱动（需要联网）。

## 功能演示

程序启动后会显示菜单：

```
1. 新增学生
2. 查看所有学生
3. 修改学生信息
4. 删除学生
5. 退出系统
```

你可以依次测试增删改查。

## 重点知识点讲解（强烈建议逐行阅读代码）

### 1. 数据库连接字符串（JDBC URL）

```java
"jdbc:mysql://localhost:3306/student_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false"
```

- `serverTimezone=Asia/Shanghai`：MySQL 8 必须指定，否则报时区错误
- `useSSL=false`：本地开发常用，生产环境要改成 true 并配置证书

### 2. try-with-resources（Java 7+ 推荐写法）

```java
try (Connection conn = ...;
     PreparedStatement ps = ...;
     ResultSet rs = ...) {
    // 业务代码
}
// 这里会自动调用 close()，不用写 finally
```

这是目前最优雅的资源释放方式。

### 3. PreparedStatement 占位符 `?`

```java
ps.setString(1, name);
ps.setInt(2, age);
```

**永远不要用字符串拼接 SQL！**

```java
// 危险写法（SQL 注入漏洞）：
String sql = "SELECT * FROM t_student WHERE name = '" + name + "'";
```

### 4. ResultSet 游标式读取

```java
while (rs.next()) {
    long id = rs.getLong("id");
    String name = rs.getString("name");
    ...
}
```

`rs.next()` 每调用一次就移动到下一行，像「指针」一样。

## 本版本的「丑陋」之处（请务必记住）

1. **所有代码写死在一个类** —— 以后功能多了会爆炸
2. **数据库配置硬编码** —— 改个密码要重新编译
3. **每次操作都新建 Connection** —— 性能极差（后面会讲连接池）
4. **没有事务** —— 多条 SQL 无法保证原子性
5. **异常处理很粗糙**
6. **重复代码极多**（获取连接、关闭资源的代码到处都是）

**正是因为上面这些问题，我们才需要后面的 demo002、demo003 去解决。**

## 下一步该做什么？

看完这个 Demo 后，**强烈建议你**：

1. 把 `StudentJdbcDemo.java` 手敲一遍（不要复制）
2. 尝试自己加一个「根据姓名模糊查询」的功能
3. 思考：如果现在要加「班级管理」功能，这个文件会变成什么样？

然后进入 **[demo002](../demo002/README.md)**，我们会开始「分层」。

---

**记住：demo001 写得越烂，后面的改进就越有成就感！**
