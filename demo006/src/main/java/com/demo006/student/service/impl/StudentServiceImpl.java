package com.demo006.student.service.impl;

import com.demo006.student.entity.Student;
import com.demo006.student.mapper.StudentMapper;
import com.demo006.student.service.StudentService;
import com.demo006.student.util.MyBatisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * =====================================================================
 * 【demo006】Service 实现类（MyBatis 注解版本）
 * =====================================================================
 *
 * 和 demo005 相比：
 * - Mapper 的实现方式从 XML 变成了注解
 * - ServiceImpl 代码几乎完全一样（这正是我们想要的效果）
 *
 * 唯一变化是 StudentMapper 接口里的 SQL 写法。
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
