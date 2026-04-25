package com.middleware.org.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.org.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志 Repository
 */
@Mapper
public interface SystemLogRepository extends BaseMapper<SystemLog> {
}