package com.middleware.org.service.impl;

import com.middleware.org.entity.SystemLog;
import com.middleware.org.model.LogEntry;
import com.middleware.org.repository.SystemLogRepository;
import com.middleware.org.service.ILogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志服务实现
 */
@Service
public class LogServiceImpl implements ILogService {

    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public void info(String taskId, String message) {
        info(taskId, "", message);
    }

    @Override
    public void info(String taskId, String stage, String message) {
        LogEntry entry = LogEntry.info(message, message);
        entry.setTaskId(taskId);
        entry.setStage(stage);
        saveToDatabase(entry);
        log.info("[{}][{}] {}", taskId, stage, message);
    }

    @Override
    public void warn(String taskId, String message) {
        warn(taskId, "", message);
    }

    @Override
    public void warn(String taskId, String stage, String message) {
        LogEntry entry = LogEntry.of("WARN", message, message);
        entry.setTaskId(taskId);
        entry.setStage(stage);
        saveToDatabase(entry);
        log.warn("[{}][{}] {}", taskId, stage, message);
    }

    @Override
    public void error(String taskId, String systemMessage, String userMessage) {
        error(taskId, "", systemMessage, userMessage);
    }

    @Override
    public void error(String taskId, String stage, String systemMessage, String userMessage) {
        LogEntry entry = LogEntry.error(systemMessage, userMessage);
        entry.setTaskId(taskId);
        entry.setStage(stage);
        saveToDatabase(entry);
        log.error("[{}][{}] {} -> {}", taskId, stage, systemMessage, userMessage);
    }

    @Override
    public void error(String taskId, String message) {
        error(taskId, "", message, message);
    }

    @Override
    public List<LogEntry> queryLogs(String taskId) {
        return systemLogRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemLog>()
                        .eq(SystemLog::getTaskId, taskId)
                        .orderByAsc(SystemLog::getTimestamp)
        ).stream().map(this::toLogEntry).collect(Collectors.toList());
    }

    @Override
    public String getTaskErrorReason(String taskId) {
        List<LogEntry> entries = queryLogs(taskId);
        for (int i = entries.size() - 1; i >= 0; i--) {
            LogEntry entry = entries.get(i);
            if ("ERROR".equals(entry.getLevel())) {
                String reason = entry.getUserMessage();
                if (reason != null && !reason.trim().isEmpty()) {
                    return reason;
                }
            }
        }
        return null;
    }

    private void saveToDatabase(LogEntry entry) {
        SystemLog systemLog = new SystemLog();
        systemLog.setLevel(entry.getLevel());
        systemLog.setMessage(entry.getMessage());
        systemLog.setUserMessage(entry.getUserMessage());
        systemLog.setTaskId(entry.getTaskId());
        systemLog.setTimestamp(entry.getTimestamp());
        systemLog.setStage(entry.getStage());
        systemLog.setExceptionMessage(entry.getExceptionMessage());
        systemLog.setStackTrace(entry.getStackTrace());
        systemLog.setCreateTime(LocalDateTime.now());
        systemLogRepository.insert(systemLog);
    }

    private LogEntry toLogEntry(SystemLog systemLog) {
        LogEntry entry = new LogEntry();
        entry.setId(systemLog.getId());
        entry.setLevel(systemLog.getLevel());
        entry.setMessage(systemLog.getMessage());
        entry.setUserMessage(systemLog.getUserMessage());
        entry.setTaskId(systemLog.getTaskId());
        entry.setTimestamp(systemLog.getTimestamp());
        entry.setStage(systemLog.getStage());
        entry.setExceptionMessage(systemLog.getExceptionMessage());
        entry.setStackTrace(systemLog.getStackTrace());
        return entry;
    }
}