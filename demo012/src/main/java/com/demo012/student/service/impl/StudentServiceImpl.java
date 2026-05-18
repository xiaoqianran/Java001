package com.demo012.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo012.student.common.exception.BusinessException;
import com.demo012.student.entity.Student;
import com.demo012.student.mapper.StudentMapper;
import com.demo012.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * =====================================================================
 * 【demo012】Service 实现类 - REST API 版本
 * =====================================================================
 *
 * Service 层保持不变，变化主要在上层 Controller（从控制台 → REST）。
 * =====================================================================
 */
@Slf4j
@Service
public class StudentServiceImpl
        extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    @Override
    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            log.warn("学生姓名为空，新增失败");
            throw new BusinessException(400, "学生姓名不能为空");
        }

        // 调用父类的 save 方法（内部会调用 mapper.insert）
        boolean success = this.save(student);
        log.info("新增学生完成，是否成功: {}", success);
        return success;
    }

    // 其他方法（getAllStudents、getStudentById、updateStudent、deleteStudent）
    // 完全可以直接使用父类提供的 list()、getById()、updateById()、removeById()
    // 这里为了保持 Controller 调用不变，我们继续暴露这些方法

    @Override
    public java.util.List<Student> getAllStudents() {
        log.debug("查询所有学生列表");
        return this.list();   // 等价于 baseMapper.selectList(null)
    }

    @Override
    public Student getStudentById(Long id) {
        log.debug("根据ID查询学生: {}", id);
        return this.getById(id);
    }

    @Override
    public boolean updateStudent(Student student) {
        log.info("准备更新学生ID: {}", student.getId());
        return this.updateById(student);
    }

    @Override
    public boolean deleteStudent(Long id) {
        log.warn("正在删除学生，ID={}", id);
        return this.removeById(id);
    }
}
