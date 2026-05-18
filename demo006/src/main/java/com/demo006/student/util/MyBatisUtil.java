package com.demo006.student.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * =====================================================================
 * 【demo005 新增】MyBatis 工具类
 * =====================================================================
 *
 * 作用：
 *   负责初始化 MyBatis 的 SqlSessionFactory（全局只需要一个）
 *   提供获取 SqlSession 的方法
 *
 * 教学注意：
 *   - 目前我们还是**手动管理** SqlSession（openSession + commit + close）
 *   - 这和之前手动管理 Connection 类似
 *   - 后面 demo007 引入 Spring 后，这部分会变得非常简单
 * =====================================================================
 */
public class MyBatisUtil {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 读取 mybatis-config.xml
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("MyBatis 初始化失败", e);
        }
    }

    /**
     * 获取一个新的 SqlSession
     * 注意：用完后必须手动 close
     */
    public static SqlSession getSqlSession() {
        // true = 自动提交（简单教学用），实际开发建议手动控制事务
        return sqlSessionFactory.openSession(true);
    }
}
