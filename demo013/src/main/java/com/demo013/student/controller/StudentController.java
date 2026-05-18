package com.demo013.student.controller;

import com.demo013.student.common.result.Result;
import com.demo013.student.controller.dto.StudentCreateDTO;
import com.demo013.student.controller.dto.StudentUpdateDTO;
import com.demo013.student.entity.Student;
import com.demo013.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo013】REST Controller + 参数校验版本
 * =====================================================================
 *
 * 核心变化：
 * 1. 新增/修改接口使用 DTO + @Valid 进行参数校验
 * 2. 校验失败时会抛出 MethodArgumentNotValidException
 * 3. 由 GlobalExceptionHandler 统一捕获并返回友好错误信息
 *
 * 好处：
 * - Controller 层不再需要手动写大量 if 判断
 * - 校验逻辑集中、规范、可复用
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
}
