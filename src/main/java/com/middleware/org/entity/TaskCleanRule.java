package com.middleware.org.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 任务-清洗规则关联实体
 */
@TableName("task_clean_rule")
public class TaskCleanRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 规则标识
     */
    private String ruleCode;

    /**
     * 执行顺序
     */
    private Integer execOrder = 0;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public TaskCleanRule() {}

    public TaskCleanRule(String taskId, String ruleCode, Integer execOrder) {
        this.taskId = taskId;
        this.ruleCode = ruleCode;
        this.execOrder = execOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Integer getExecOrder() {
        return execOrder;
    }

    public void setExecOrder(Integer execOrder) {
        this.execOrder = execOrder;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}