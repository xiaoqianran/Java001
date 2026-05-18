package com.demo013.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo013.student.entity.Student;

/**
 * =====================================================================
 * 【demo009 核心】StudentMapper - MyBatis-Plus 版本
 * =====================================================================
 *
 * **巨大的简化！**
 *
 * 以前（demo005 ~ demo008）：
 *   - 要写 5 个方法的 SQL（insert, selectById, selectAll, update, deleteById）
 *   - 无论是 XML 还是注解，都要手写
 *
 * 现在：
 *   - 什么都不用写！
 *   - 直接继承 BaseMapper<Student>
 *
 * BaseMapper 已经提供了：
 *   - insert, delete, deleteById, updateById, selectById, selectList, selectPage 等 20+ 个方法
 *
 * 这就是 MyBatis-Plus 最大的魅力：**让 80% 的 CRUD 代码消失**。
 * =====================================================================
 */
public interface StudentMapper extends BaseMapper<Student> {
    // 空的！所有常用 CRUD 都已经由 BaseMapper 提供
}
