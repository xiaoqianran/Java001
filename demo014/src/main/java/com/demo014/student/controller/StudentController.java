package com.demo014.student.controller;

import com.demo014.student.common.result.Result;
import com.demo014.student.controller.dto.StudentCreateDTO;
import com.demo014.student.controller.dto.StudentUpdateDTO;
import com.demo014.student.entity.Student;
import com.demo014.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo014】REST Controller + 分页查询版本
 * =====================================================================
 *
 * 在 demo013 基础上新增：
 * - GET /students/page 支持分页 + 姓名模糊搜索
 * - 使用 MyBatis-Plus 的 Page 和 LambdaQueryWrapper
 *
 * 这是真实项目中最常用的列表查询方式。
 * =====================================================================
 */
@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public Result<List<Student>> list() {
        return Result.success(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return student != null ? Result.success(student) : Result.error(404, "学生不存在");
    }

    /**
     * 新增学生 - 使用 DTO + @Valid
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid StudentCreateDTO dto) {
        log.info("REST 新增学生: {}", dto.getName());

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);

        boolean success = studentService.addStudent(student);
        return success ? 
            Result.success("新增成功", student.getId()) : 
            Result.error("新增失败");
    }

    /**
     * 修改学生
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid StudentUpdateDTO dto) {
        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        student.setId(id);

        boolean success = studentService.updateStudent(student);
        return success ? Result.success(true) : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = studentService.deleteStudent(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 分页查询 + 姓名模糊搜索
     * 示例：GET /students/page?page=1&size=10&name=张
     */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> result = studentService.getStudentPage(page, size, name);
        return Result.success(result);
    }
}
