package com.demo002.student.mapper.impl;

import com.demo002.student.entity.Student;
import com.demo002.student.mapper.StudentMapper;
import com.demo002.student.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * 【Mapper 实现类】StudentMapperImpl
 * =====================================================================
 *
 * 这一层是「最脏最累」的层，负责：
 *   - 写 SQL
 *   - 设置参数
 *   - 执行 SQL
 *   - 把 ResultSet 手动映射成 Student 对象
 *
 * 教学要点：
 *   - 观察这里和 demo001 里的 JDBC 代码几乎一模一样
 *   - 但现在这些代码被「封装」起来了，上层（Service）不用再关心
 * =====================================================================
 */
public class StudentMapperImpl implements StudentMapper {

    // ==================== 新增 ====================
    @Override
    public int insert(Student student) {
        String sql = "INSERT INTO t_student(name, age, gender, email, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setObject(2, student.getAge());
            ps.setObject(3, student.getGender());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("新增学生失败", e);
        }
    }

    // ==================== 根据 ID 查询 ====================
    @Override
    public Student selectById(Long id) {
        String sql = "SELECT * FROM t_student WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);   // 把当前行转换成 Student 对象
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        }
    }

    // ==================== 查询全部 ====================
    @Override
    public List<Student> selectAll() {
        String sql = "SELECT * FROM t_student ORDER BY id";

        List<Student> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("查询学生列表失败", e);
        }
    }

    // ==================== 更新 ====================
    @Override
    public int update(Student student) {
        String sql = "UPDATE t_student SET name=?, age=?, gender=?, email=?, phone=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setObject(2, student.getAge());
            ps.setObject(3, student.getGender());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            ps.setLong(6, student.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("更新学生失败", e);
        }
    }

    // ==================== 删除 ====================
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM t_student WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("删除学生失败", e);
        }
    }

    // =================================================================
    // 【私有工具方法】把 ResultSet 当前行映射成 Student 对象
    // =================================================================
    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setName(rs.getString("name"));
        student.setAge(rs.getInt("age"));
        student.setGender(rs.getInt("gender"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setCreateTime(rs.getTimestamp("create_time"));
        student.setUpdateTime(rs.getTimestamp("update_time"));
        return student;
    }
}
