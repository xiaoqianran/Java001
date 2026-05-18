package com.demo006.student.mapper;

import com.demo006.student.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo006 核心】StudentMapper 接口 - 注解方式
 * =====================================================================
 *
 * 和 demo005 的最大区别：
 *
 * demo005：SQL 写在 StudentMapper.xml 文件中
 * demo006：SQL 直接写在接口方法的注解上（@Select / @Insert / @Update / @Delete）
 *
 * 优点：
 * - 不用再维护 XML 文件
 * - SQL 和 Java 方法写在一起，更直观（小项目常用）
 *
 * 缺点：
 * - 复杂 SQL 写在注解里会很丑（这时推荐回 XML）
 * - 无法像 XML 那样方便地做动态 SQL（需要 @Provider 或 XML）
 *
 * 本 Demo 教学目的：让你清晰对比 XML 和 Annotation 两种写法。
 * =====================================================================
 */
public interface StudentMapper {

    @Insert("INSERT INTO t_student(name, age, gender, email, phone) " +
            "VALUES (#{name}, #{age}, #{gender}, #{email}, #{phone})")
    int insert(Student student);

    @Select("SELECT id, name, age, gender, email, phone, create_time, update_time " +
            "FROM t_student WHERE id = #{id}")
    Student selectById(Long id);

    @Select("SELECT id, name, age, gender, email, phone, create_time, update_time " +
            "FROM t_student ORDER BY id")
    List<Student> selectAll();

    @Update("UPDATE t_student SET name=#{name}, age=#{age}, gender=#{gender}, " +
            "email=#{email}, phone=#{phone} WHERE id = #{id}")
    int update(Student student);

    @Delete("DELETE FROM t_student WHERE id = #{id}")
    int deleteById(Long id);
}
