package com.demo012.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo012.student.entity.Student;

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
}
