package com.demo019.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo019.student.common.exception.BusinessException;
import com.demo019.student.entity.Student;
import com.demo019.student.mapper.StudentMapper;
import com.demo019.student.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * =====================================================================
 * 【demo015】Service 实现类 - 事务管理版本
 * =====================================================================
 *
 * 新增 batchAddStudents 方法，使用 @Transactional 演示事务回滚。
 * =====================================================================
 */
@Slf4j
@Service
public class StudentServiceImpl
        extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存空值的 key 前缀（防止缓存穿透）
    private static final String NULL_VALUE = "NULL";
    private static final long NULL_VALUE_TTL = 60; // 空值缓存 60 秒

    @Override
    public boolean addStudent(Student student) {
        log.info("准备新增学生: {}", student.getName());

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            log.warn("学生姓名为空，新增失败");
            throw new BusinessException(400, "学生姓名不能为空");
        }

        // 调用父类的 save 方法（内部会调用 mapper.insert）
        boolean success = this.save(student);
        log.info("新增学生完成，是否成功: {}", success);
        return success;
    }

    // 其他方法（getAllStudents、getStudentById、updateStudent、deleteStudent）
    // 完全可以直接使用父类提供的 list()、getById()、updateById()、removeById()
    // 这里为了保持 Controller 调用不变，我们继续暴露这些方法

    @Override
    public java.util.List<Student> getAllStudents() {
        log.debug("查询所有学生列表");
        return this.list();   // 等价于 baseMapper.selectList(null)
    }

    /**
     * 根据ID查询学生（带缓存 + 缓存穿透保护）
     *
     * @Cacheable: 先查缓存，命中直接返回；未命中执行方法并把结果写入 Redis
     * key = "'student:' + #id"  → 最终 Redis key: student:15
     */
    @Override
    @Cacheable(value = "student", key = "'student:' + #id", unless = "#result == null")
    public Student getStudentById(Long id) {
        log.debug("【未命中缓存】从数据库查询学生: {}", id);
        return this.getById(id);
    }

    @Override
    @CacheEvict(value = "student", key = "'student:' + #student.id")
    public boolean updateStudent(Student student) {
        log.info("准备更新学生ID: {}（清除缓存）", student.getId());
        return this.updateById(student);
    }

    @Override
    @CacheEvict(value = "student", key = "'student:' + #id")
    public boolean deleteStudent(Long id) {
        log.warn("正在删除学生，ID={}（清除缓存）", id);
        return this.removeById(id);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> getStudentPage(Integer page, Integer size, String name) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Student::getName, name);
        }

        queryWrapper.orderByDesc(Student::getId);

        return this.page(pageParam, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddStudents(java.util.List<Student> students) {
        log.info("开始批量新增学生，共 {} 条", students.size());

        int count = 0;
        for (Student student : students) {
            // 演示回滚：如果名字包含 "rollback" 就抛异常
            if (student.getName() != null && student.getName().contains("rollback")) {
                throw new com.demo019.student.common.exception.BusinessException(400, "模拟业务异常，触发事务回滚");
            }

            boolean saved = this.save(student);
            if (saved) count++;
        }

        log.info("批量新增完成，成功 {} 条", count);
        return count;
    }

    // ==================== 缓存穿透保护演示方法 ====================

    /**
     * 带缓存穿透保护的查询（手动 Redis 操作演示）
     *
     * 解决缓存穿透的经典做法：
     * 1. 查询 DB 后，如果结果为 null，仍然往 Redis 写一个短 TTL 的占位符（NULL）
     * 2. 下次同样不存在的请求直接命中缓存的 NULL，不再打到 DB
     * 3. 同时使用随机 TTL 防止大量 key 同时过期导致缓存雪崩
     */
    public Student getStudentByIdWithProtection(Long id) {
        String cacheKey = "student:detail:" + id;

        // 1. 先查 Redis
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (NULL_VALUE.equals(cached)) {
                log.info("【缓存穿透保护】命中空值缓存，id={}", id);
                return null;
            }
            log.info("【缓存命中】从 Redis 获取学生 id={}", id);
            return (Student) cached;
        }

        // 2. Redis 没有，查数据库
        log.info("【缓存未命中】从数据库查询 id={}", id);
        Student student = this.getById(id);

        // 3. 写入缓存（包含空值保护 + 随机 TTL 防雪崩）
        long ttl = 300 + (long) (Math.random() * 120); // 5~7 分钟随机
        if (student != null) {
            redisTemplate.opsForValue().set(cacheKey, student, ttl, TimeUnit.SECONDS);
        } else {
            // 关键！缓存空值，防止大量不存在的 id 一直打 DB
            redisTemplate.opsForValue().set(cacheKey, NULL_VALUE, NULL_VALUE_TTL, TimeUnit.SECONDS);
            log.warn("【缓存穿透保护】该学生不存在，已缓存空值占位，id={}", id);
        }

        return student;
    }
}
