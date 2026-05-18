package com.demo019.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo019.student.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysUser Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}