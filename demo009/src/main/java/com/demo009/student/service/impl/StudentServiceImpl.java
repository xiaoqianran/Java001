package com.demo009.student.service.impl;

import com.demo009.student.entity.Student;
import com.demo009.student.mapper.StudentMapper;
import com.demo009.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =====================================================================
 * 【demo009】Service 实现类 - MyBatis-Plus 极简版
 * =====================================================================
 *
 * **再次大幅简化！**
 *
 * 以前（demo008）：
 *   studentMapper.insert(student)
 *   studentMapper.selectById(id)
 *   studentMapper.selectAll()
 *   studentMapper.update(student)
 *   studentMapper.deleteById(id)
 *
 * 现在使用 MyBatis-Plus 提供的标准方法：
 *   - save()          → 新增
 *   - getById()       → 根据ID查询
 *   - list()          → 查询所有
 *   - updateById()    → 根据ID更新
 *   - removeById()    → 根据ID删除
 *
 * 代码量进一步减少，可读性更强。
 * =====================================================================
 */
@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            log.warn("学生姓名为空，新增失败");
            throw new IllegalArgumentException("学生姓名不能为空");
        }

        // MyBatis-Plus 的 save 方法
        boolean success = studentMapper.insert(student) > 0;
        log.info("新增学生完成，是否成功: {}", success);
        return success;
    }

    @Override
    public List<Student> getAllStudents() {
        log.debug("查询所有学生列表");
        // list() 默认查询所有（无条件）
        return studentMapper.selectList(null);
    }

    @Override
    public Student getStudentById(Long id) {
        log.debug("根据ID查询学生: {}", id);
        return studentMapper.selectById(id);
    }

    @Override
    public boolean updateStudent(Student student) {
        log.info("准备更新学生ID: {}", student.getId());
        // updateById 会根据主键更新非空字段
        return studentMapper.updateById(student) > 0;
    }

    @Override
    public boolean deleteStudent(Long id) {
        log.warn("正在删除学生，ID={}", id);
        return studentMapper.deleteById(id) > 0;
    }
}
