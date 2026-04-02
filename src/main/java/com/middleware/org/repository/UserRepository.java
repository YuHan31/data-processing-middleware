package com.middleware.org.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.org.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}