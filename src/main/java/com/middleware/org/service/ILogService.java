package com.middleware.org.service;

import com.middleware.org.model.LogEntry;

import java.util.List;

/**
 * 日志服务接口
 */
public interface ILogService {

    /**
     * 记录信息日志
     */
    void info(String taskId, String message);

    /**
     * 记录警告日志
     */
    void warn(String taskId, String message);

    /**
     * 记录错误日志
     */
    void error(String taskId, String message);

    /**
     * 查询日志
     */
    List<LogEntry> queryLogs(String taskId);
}
