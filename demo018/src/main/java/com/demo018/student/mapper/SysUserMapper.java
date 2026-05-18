package com.demo018.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo018.student.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysUser Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}