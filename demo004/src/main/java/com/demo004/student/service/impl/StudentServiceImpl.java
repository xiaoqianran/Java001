package com.demo004.student.service.impl;

import com.demo004.student.entity.Student;
import com.demo004.student.mapper.StudentMapper;
import com.demo004.student.mapper.impl.StudentMapperImpl;
import com.demo004.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * =====================================================================
 * 【demo004 教学重点】在 demo003 的 Lombok 基础上，只增加了日志功能
 * =====================================================================
 *
 * 本文件的变化：
 *   - 增加了 @Slf4j 注解
 *   - 把关键操作点换成了 log.info()、log.warn()、log.debug()
 *
 * 这是「在已有 Lombok 的项目上，再加入日志框架」这一小步。
 * 目的是让你清晰感受到每一次只改一件事的教学节奏。
 *
 * 注意：Entity 层没有任何改动，继续使用 demo003 的 @Data + @Builder 版本。
 * =====================================================================
 */
@Slf4j
public class StudentServiceImpl implements StudentService {

    // 目前仍然是手动装配，后面学 Spring 后会改成构造器注入
    private final StudentMapper studentMapper = new StudentMapperImpl();

    @Override
    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            log.warn("学生姓名为空，新增失败");
            throw new IllegalArgumentException("学生姓名不能为空");
        }

        int rows = studentMapper.insert(student);
        log.info("新增学生完成，影响行数: {}", rows);
        return rows > 0;
    }

    @Override
    public List<Student> getAllStudents() {
        log.debug("查询所有学生列表");
        return studentMapper.selectAll();
    }

    @Override
    public Student getStudentById(Long id) {
        log.debug("根据ID查询学生: {}", id);
        return studentMapper.selectById(id);
    }

    @Override
    public boolean updateStudent(Student student) {
        log.info("准备更新学生ID: {}", student.getId());
        int rows = studentMapper.update(student);
        return rows > 0;
    }

    @Override
    public boolean deleteStudent(Long id) {
        log.warn("正在删除学生，ID={}", id);
        int rows = studentMapper.deleteById(id);
        return rows > 0;
    }
}
