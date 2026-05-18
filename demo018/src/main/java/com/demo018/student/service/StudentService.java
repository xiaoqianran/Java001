package com.demo018.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo018.student.entity.Student;

import java.util.List;

/**
 * =====================================================================
 * 【demo010】StudentService 接口 - MyBatis-Plus IService 版本
 * =====================================================================
 *
 * 我们选择保留原有的业务方法声明（方便上层 Controller 不变），
 * 同时继承 IService，获得框架提供的大量通用方法。
 * =====================================================================
 */
public interface StudentService extends IService<Student> {

    /**
     * 新增学生（带业务校验）
     */
    boolean addStudent(Student student);

    /**
     * 查询所有学生
     */
    List<Student> getAllStudents();

    /**
     * 根据ID查询
     */
    Student getStudentById(Long id);

    /**
     * 修改学生
     */
    boolean updateStudent(Student student);

    /**
     * 删除学生
     */
    boolean deleteStudent(Long id);

    /**
     * 分页查询学生（支持姓名模糊搜索）
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> getStudentPage(Integer page, Integer size, String name);

    /**
     * 批量新增学生（演示事务）
     * 如果列表中任何学生姓名包含 "rollback"，将触发回滚
     */
    int batchAddStudents(java.util.List<Student> students);

    /**
     * 带缓存穿透保护的查询（手动 Redis 实现，演示 null 缓存 + 随机 TTL）
     */
    Student getStudentByIdWithProtection(Long id);
}
