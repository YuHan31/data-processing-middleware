package com.middleware.org.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.org.entity.TaskCleanRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务规则关联 Repository
 */
@Mapper
public interface TaskCleanRuleRepository extends BaseMapper<TaskCleanRule> {

    /**
     * 查询任务关联的规则列表，按执行顺序排序
     */
    @Select("SELECT * FROM task_clean_rule WHERE task_id = #{taskId} ORDER BY exec_order ASC")
    List<TaskCleanRule> findByTaskIdOrderByExecOrder(@Param("taskId") String taskId);

    /**
     * 删除任务关联的所有规则
     */
    @Select("DELETE FROM task_clean_rule WHERE task_id = #{taskId}")
    void deleteByTaskId(@Param("taskId") String taskId);
}