package com.demo005.student.mapper;

import com.demo005.student.entity.Student;

import java.util.List;

/**
 * =====================================================================
 * 【demo005 重点】StudentMapper 接口（MyBatis 版本）
 * =====================================================================
 *
 * 重大变化：
 * - 以前这个接口的实现是 StudentMapperImpl.java（里面写满 JDBC 代码）
 * - 现在这个接口**没有实现类**了！
 *
 * MyBatis 会根据这个接口 + StudentMapper.xml 动态生成实现类。
 *
 * 接口里的方法名 + 参数类型 + 返回类型 必须和 XML 里的 id 严格对应。
 * =====================================================================
 */
public interface StudentMapper {

    /**
     * 新增学生
     * @return 插入成功返回 1，失败返回 0
     */
    int insert(Student student);

    /**
     * 根据主键查询
     */
    Student selectById(Long id);

    /**
     * 查询所有学生
     */
    List<Student> selectAll();

    /**
     * 更新学生信息
     */
    int update(Student student);

    /**
     * 根据主键删除
     */
    int deleteById(Long id);
}
