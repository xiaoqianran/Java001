package com.demo008.student.service;

import com.demo008.student.entity.Student;

import java.util.List;

/**
 * =====================================================================
 * 【Service 接口】学生业务逻辑层
 * =====================================================================
 *
 * Service 层的作用：
 *   - 放「业务规则」
 *   - 组合多个 Mapper 操作（比如先查再改）
 *   - 事务控制（目前这个版本还没加事务）
 *   - 对上层（Controller）隐藏底层实现细节
 *
 * 目前这个版本的 Service 还比较「薄」，大部分逻辑还是在 Mapper 里。
 * 后面学了事务、校验、缓存后，Service 就会越来越「胖」。
 * =====================================================================
 */
public interface StudentService {

    /**
     * 新增学生（可以在这里加各种业务校验）
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
