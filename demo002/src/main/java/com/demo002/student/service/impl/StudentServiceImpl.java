package com.demo002.student.service.impl;

import com.demo002.student.entity.Student;
import com.demo002.student.mapper.StudentMapper;
import com.demo002.student.mapper.impl.StudentMapperImpl;
import com.demo002.student.service.StudentService;

import java.util.List;

/**
 * =====================================================================
 * 【Service 实现类】
 * =====================================================================
 *
 * 注意这里的「手动装配」：
 *   private final StudentMapper studentMapper = new StudentMapperImpl();
 *
 * 这就是「没有 Spring」时的真实写法。
 * 后面学 Spring 后，这一行会消失，全部交给 @Autowired / 构造器注入。
 * =====================================================================
 */
public class StudentServiceImpl implements StudentService {

    // 这里是「手动 new」，没有依赖注入
    private final StudentMapper studentMapper = new StudentMapperImpl();

    @Override
    public boolean addStudent(Student student) {
        // 简单业务校验示例
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        if (student.getAge() != null && (student.getAge() < 0 || student.getAge() > 150)) {
            throw new IllegalArgumentException("年龄不合法");
        }

        int rows = studentMapper.insert(student);
        return rows > 0;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.selectAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public boolean updateStudent(Student student) {
        if (student.getId() == null) {
            throw new IllegalArgumentException("修改时学生ID不能为空");
        }
        int rows = studentMapper.update(student);
        return rows > 0;
    }

    @Override
    public boolean deleteStudent(Long id) {
        int rows = studentMapper.deleteById(id);
        return rows > 0;
    }
}
