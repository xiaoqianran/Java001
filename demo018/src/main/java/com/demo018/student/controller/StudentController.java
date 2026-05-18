package com.demo018.student.controller;

import com.demo018.student.common.log.TraceIdUtil;
import com.demo018.student.common.result.Result;
import com.demo018.student.controller.dto.StudentCreateDTO;
import com.demo018.student.controller.dto.StudentUpdateDTO;
import com.demo018.student.entity.Student;
import com.demo018.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo016】REST Controller（已接入统一日志）
 * =====================================================================
 *
 * 演示：
 * - 如何在 Controller 中配合 MDC traceId 打印业务日志
 * - 所有请求现在都会被 LoggingInterceptor 自动记录
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
        log.info("查询学生列表，当前 traceId={}", TraceIdUtil.get());
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
        log.info("REST 新增学生: {}，traceId={}", dto.getName(), TraceIdUtil.get());

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

    /**
     * 批量新增学生（演示事务）
     * 请求体示例：
     * [
     *   {"name": "学生1", "age": 20, "gender": 1},
     *   {"name": "rollback测试", "age": 21, "gender": 0}
     * ]
     * 第二个会触发回滚
     */
    @PostMapping("/batch")
    public Result<Integer> batchAdd(@RequestBody java.util.List<Student> students) {
        log.info("REST 批量新增学生请求，数量: {}", students.size());
        int successCount = studentService.batchAddStudents(students);
        return Result.success("批量新增完成", successCount);
    }

    /**
     * 演示带缓存穿透保护的查询（手动 Redis 实现）
     * 访问不存在的 ID 多次，观察是否仍然打 DB
     * GET /students/protect/{id}
     */
    @GetMapping("/protect/{id}")
    public Result<Student> getWithProtection(@PathVariable Long id) {
        Student student = studentService.getStudentByIdWithProtection(id);
        return student != null ? Result.success(student) : Result.error(404, "学生不存在（已缓存空值）");
    }
}
