package com.demo008.student.service.impl;

import com.demo008.student.entity.Student;
import com.demo008.student.mapper.StudentMapper;
import com.demo008.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =====================================================================
 * 【demo008】Service 实现类 - Spring Boot 最终版
 * =====================================================================
 *
 * 在 Spring Boot + mybatis-spring-boot-starter 的帮助下，
 * 代码已经精简到极致。
 *
 * 所有配置（数据源、MyBatis、Mapper 扫描）都由框架自动完成。
 * =====================================================================
 */
@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    /**
     * 直接注入 Mapper！
     * MyBatis-Spring 会自动创建代理并注入进来。
     * 再也不需要 new，也不需要手动开 session 了。
     */
    @Autowired
    private StudentMapper studentMapper;

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
