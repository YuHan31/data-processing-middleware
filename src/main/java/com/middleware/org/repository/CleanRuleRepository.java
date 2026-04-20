package com.middleware.org.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.org.entity.CleanRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 清洗规则 Repository
 */
@Mapper
public interface CleanRuleRepository extends BaseMapper<CleanRule> {
}