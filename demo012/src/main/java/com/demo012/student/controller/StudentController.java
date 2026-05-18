package com.demo012.student.controller;

import com.demo012.student.common.result.Result;
import com.demo012.student.entity.Student;
import com.demo012.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo012 核心】RESTful API Controller
 * =====================================================================
 *
 * 这是从控制台程序彻底转型为 Web API 的关键一步。
 *
 * 常用 REST 设计规范：
 *   GET    /students          -> 查询所有
 *   GET    /students/{id}     -> 根据ID查询
 *   POST   /students          -> 新增
 *   PUT    /students/{id}     -> 修改
 *   DELETE /students/{id}     -> 删除
 *
 * 所有接口统一返回 Result<T>，配合 GlobalExceptionHandler 实现优雅异常处理。
 * =====================================================================
 */
@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 查询所有学生
     */
    @GetMapping
    public Result<List<Student>> list() {
        log.info("REST 请求：查询所有学生");
        List<Student> list = studentService.getAllStudents();
        return Result.success(list);
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        log.info("REST 请求：查询学生 ID={}", id);
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return Result.error(404, "学生不存在");
        }
        return Result.success(student);
    }

    /**
     * 新增学生
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Student student) {
        log.info("REST 请求：新增学生");
        boolean success = studentService.addStudent(student);
        return success ? Result.success("新增成功", true) : Result.error("新增失败");
    }

    /**
     * 修改学生
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Student student) {
        log.info("REST 请求：修改学生 ID={}", id);
        student.setId(id);
        boolean success = studentService.updateStudent(student);
        return success ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("REST 请求：删除学生 ID={}", id);
        boolean success = studentService.deleteStudent(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }
}
