package com.demo007.student.service.impl;

import com.demo007.student.entity.Student;
import com.demo007.student.mapper.StudentMapper;
import com.demo007.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =====================================================================
 * 【demo007 教学核心】Service 实现类 - MyBatis-Spring 版本
 * =====================================================================
 *
 * **这是整个系列迄今为止最大的进步！**
 *
 * 以前（demo001 ~ demo006）：
 *   - 每次方法都要写 try (SqlSession session = MyBatisUtil.getSqlSession())
 *   - 手动获取 Mapper
 *   - 手动管理资源关闭
 *
 * 现在（demo007）：
 *   - 直接注入 StudentMapper
 *   - 什么都不用管，Spring + MyBatis-Spring 帮我们全部搞定
 *   - 代码变得非常干净
 *
 * 这就是依赖注入（DI）和 IoC 容器的威力。
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
