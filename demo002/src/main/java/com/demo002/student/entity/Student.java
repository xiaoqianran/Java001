package com.demo002.student.entity;

import java.util.Date;

/**
 * =====================================================================
 * 【Entity / POJO】学生实体类
 * =====================================================================
 *
 * 作用：
 *   - 把数据库里的一行记录「映射」成一个 Java 对象
 *   - 以后 Service、Controller 都只和这个对象打交道，不直接碰数据库字段
 *
 * 本版本特点（重要！）：
 *   - 完全手动编写 getter / setter / toString / 构造方法
 *   - 没有任何 Lombok 注解
 *   - 你能清楚看到一个「普通 Java 对象」到底需要写多少代码
 *
 * 对比 demo003：当我们加上 Lombok 后，这个文件会只剩 15 行！
 * =====================================================================
 */
public class Student {

    // ==================== 字段定义 ====================
    private Long id;           // 主键
    private String name;       // 姓名
    private Integer age;       // 年龄
    private Integer gender;    // 性别 0女 1男
    private String email;      // 邮箱
    private String phone;      // 手机号
    private Date createTime;   // 创建时间
    private Date updateTime;   // 更新时间

    // ==================== 构造方法 ====================

    /**
     * 无参构造（必须要有）
     * 很多框架（MyBatis、Jackson 等）在反射创建对象时都需要无参构造
     */
    public Student() {
    }

    /**
     * 全参构造（方便测试时快速创建对象）
     */
    public Student(Long id, String name, Integer age, Integer gender,
                   String email, String phone, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== Getter & Setter（全部手写） ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    // ==================== 其他常用方法 ====================

    /**
     * 重写 toString，方便调试时打印对象内容
     */
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
