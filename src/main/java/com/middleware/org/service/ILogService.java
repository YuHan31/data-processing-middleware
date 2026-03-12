package com.middleware.org.service;

import com.middleware.org.model.LogEntry;

import java.util.List;

/**
 * 日志与异常处理接口
 * 贯穿系统运行全过程，实现系统运行状态记录与异常追踪
 */
public interface ILogService {

    /**
     * 记录信息日志
     * @param message 日志消息
     */
    void info(String message);

    /**
     * 记录警告日志
     * @param message 日志消息
     */
    void warn(String message);

    /**
     * 记录错误日志
     * @param message 日志消息
     */
    void error(String message);

    /**
     * 记录错误日志（带异常）
     * @param message 日志消息
     * @param throwable 异常对象
     */
    void error(String message, Throwable throwable);

    /**
     * 查询日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param level 日志级别
     * @return 日志条目列表
     */
    List<LogEntry> queryLogs(Long startTime, Long endTime, String level);

    /**
     * 清理过期日志
     * @param retentionDays 保留天数
     */
    void cleanExpiredLogs(int retentionDays);
}
