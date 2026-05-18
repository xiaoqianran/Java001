package com.demo005.student.service.impl;

import com.demo005.student.entity.Student;
import com.demo005.student.mapper.StudentMapper;
import com.demo005.student.service.StudentService;
import com.demo005.student.util.MyBatisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * =====================================================================
 * 【demo005】Service 实现类（MyBatis 版本）
 * =====================================================================
 *
 * **这一步的核心变化**：
 *
 * 以前：
 *   private final StudentMapper studentMapper = new StudentMapperImpl();
 *
 * 现在：
 *   每次方法里通过 MyBatisUtil 拿到 SqlSession，再获取 Mapper 代理对象
 *
 * 为什么每次都 new SqlSession？
 *   - 教学阶段最简单、最直观
 *   - 后面引入 Spring 后会改成单例 + 事务管理
 *
 * 注意：上层（Controller）完全没有变化！这体现了分层的价值。
 * =====================================================================
 */
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Override
    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            log.warn("学生姓名为空，新增失败");
            throw new IllegalArgumentException("学生姓名不能为空");
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);
            int rows = mapper.insert(student);
            log.info("新增学生完成，影响行数: {}", rows);
            return rows > 0;
        }
    }

    @Override
    public List<Student> getAllStudents() {
        log.debug("查询所有学生列表");
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);
            return mapper.selectAll();
        }
    }

    @Override
    public Student getStudentById(Long id) {
        log.debug("根据ID查询学生: {}", id);
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);
            return mapper.selectById(id);
        }
    }

    @Override
    public boolean updateStudent(Student student) {
        log.info("准备更新学生ID: {}", student.getId());
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);
            int rows = mapper.update(student);
            return rows > 0;
        }
    }

    @Override
    public boolean deleteStudent(Long id) {
        log.warn("正在删除学生，ID={}", id);
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);
            int rows = mapper.deleteById(id);
            return rows > 0;
        }
    }
}
