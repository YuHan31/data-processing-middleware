package com.middleware.org.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.org.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务 Mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM task WHERE task_id = #{taskId}")
    Task findByTaskId(@Param("taskId") String taskId);

    @Select("SELECT * FROM task WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Task> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM task ORDER BY create_time DESC")
    List<Task> findAll();

    @Select("UPDATE task SET status = #{status} WHERE task_id = #{taskId}")
    void updateStatus(@Param("taskId") String taskId, @Param("status") String status);

    @Select("UPDATE task SET start_time = #{startTime}, end_time = #{endTime} WHERE task_id = #{taskId}")
    void updateTimes(@Param("taskId") String taskId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("DELETE FROM task WHERE task_id = #{taskId}")
    void deleteByTaskId(@Param("taskId") String taskId);
}