package com.middleware.org.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体
 */
@Data
@TableName("system_log")
public class SystemLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String level;      // INFO, WARN, ERROR
    private String message;    // 系统原始日志
    private String userMessage; // 用户友好提示
    private String taskId;
    private Long timestamp;
    private String stage;      // PARSE, CLEAN, EXPORT
    private String exceptionMessage;
    private String stackTrace;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}