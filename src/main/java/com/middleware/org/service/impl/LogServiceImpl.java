package com.middleware.org.service.impl;

import com.middleware.org.model.LogEntry;
import com.middleware.org.service.ILogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志服务实现
 */
@Service
public class LogServiceImpl implements ILogService {

    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);

    private final Map<String, List<LogEntry>> logStore = new ConcurrentHashMap<>();

    @Override
    public void info(String taskId, String message) {
        LogEntry entry = LogEntry.info(message, message);
        entry.setTaskId(taskId);
        addLog(taskId, entry);
        log.info("[{}] {}", taskId, message);
    }

    @Override
    public void warn(String taskId, String message) {
        LogEntry entry = LogEntry.of("WARN", message, message);
        entry.setTaskId(taskId);
        addLog(taskId, entry);
        log.warn("[{}] {}", taskId, message);
    }

    @Override
    public void error(String taskId, String systemMessage, String userMessage) {
        LogEntry entry = LogEntry.error(systemMessage, userMessage);
        entry.setTaskId(taskId);
        addLog(taskId, entry);
        log.error("[{}] {} -> {}", taskId, systemMessage, userMessage);
    }

    @Override
    public void error(String taskId, String message) {
        // 兼容旧调用，message 同时作为系统消息和用户消息
        error(taskId, message, message);
    }

    @Override
    public List<LogEntry> queryLogs(String taskId) {
        return logStore.getOrDefault(taskId, new ArrayList<>());
    }

    @Override
    public String getTaskErrorReason(String taskId) {
        List<LogEntry> entries = logStore.getOrDefault(taskId, new ArrayList<>());
        // 返回最后一条 ERROR 的用户友好信息
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

    private void addLog(String taskId, LogEntry entry) {
        logStore.computeIfAbsent(taskId, k -> new ArrayList<>()).add(entry);
    }
}