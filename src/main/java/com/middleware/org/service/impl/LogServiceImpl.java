package com.middleware.org.service.impl;

import com.middleware.org.model.LogEntry;
import com.middleware.org.service.ILogService;
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

    private final Map<String, List<LogEntry>> logStore = new ConcurrentHashMap<>();

    @Override
    public void info(String taskId, String message) {
        addLog(taskId, "INFO", message);
        System.out.println("[INFO] [" + taskId + "] " + message);
    }

    @Override
    public void warn(String taskId, String message) {
        addLog(taskId, "WARN", message);
        System.out.println("[WARN] [" + taskId + "] " + message);
    }

    @Override
    public void error(String taskId, String message) {
        addLog(taskId, "ERROR", message);
        System.err.println("[ERROR] [" + taskId + "] " + message);
    }

    @Override
    public List<LogEntry> queryLogs(String taskId) {
        return logStore.getOrDefault(taskId, new ArrayList<>());
    }

    private void addLog(String taskId, String level, String message) {
        LogEntry entry = new LogEntry(level, message);
        logStore.computeIfAbsent(taskId, k -> new ArrayList<>()).add(entry);
    }
}
