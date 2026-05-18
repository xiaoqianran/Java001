package com.demo009.student;

import com.demo009.student.controller.StudentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo009】Spring Boot + MyBatis-Plus 启动入口
 * =====================================================================
 *
 * 相比 demo008：
 * - 依赖从 mybatis-spring-boot-starter 换成了 mybatis-plus-spring-boot3-starter
 * - Mapper 继承了 BaseMapper，代码进一步减少
 *
 * 运行方式不变。
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private StudentController studentController;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Spring Boot 应用启动完成，进入学生管理系统...");
        studentController.start();
    }
}
