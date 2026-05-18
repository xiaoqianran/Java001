package com.demo002.student.mapper;

import com.demo002.student.entity.Student;

import java.util.List;

/**
 * =====================================================================
 * 【Mapper / DAO 接口】学生数据访问层接口
 * =====================================================================
 *
 * 作用：
 *   定义「对学生表的所有数据库操作」有哪些方法
 *   具体实现写在 StudentMapperImpl 里
 *
 * 为什么要有接口？
 *   - 解耦：Service 层只依赖接口，不依赖具体实现
 *   - 方便以后切换成 MyBatis、JPA 等其他实现（这就是 demo004 要做的事）
 *   - 也方便写单元测试时 Mock
 *
 * 命名说明：
 *   - 很多公司把这一层叫 DAO（Data Access Object）
 *   - 我们这里叫 Mapper，是为了和后面 MyBatis 的命名保持一致
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
