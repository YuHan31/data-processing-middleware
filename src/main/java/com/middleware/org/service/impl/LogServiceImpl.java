package com.middleware.org.service.impl;

import com.middleware.org.model.LogEntry;
import com.middleware.org.service.ILogService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日志与异常处理服务实现
 */
public class LogServiceImpl implements ILogService {

    private static final String LOG_DIR = "logs/";
    private static final String LOG_FILE = LOG_DIR + "application.log";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogServiceImpl() {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void info(String message) {
        writeLog("INFO", message, null);
    }

    @Override
    public void warn(String message) {
        writeLog("WARN", message, null);
    }

    @Override
    public void error(String message) {
        writeLog("ERROR", message, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        writeLog("ERROR", message, throwable);
    }

    @Override
    public List<LogEntry> queryLogs(Long startTime, Long endTime, String level) {
        List<LogEntry> logs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = parseLogLine(line);
                if (entry != null) {
                    if (level != null && !level.equals(entry.getLevel())) {
                        continue;
                    }
                    if (startTime != null && entry.getTimestamp() < startTime) {
                        continue;
                    }
                    if (endTime != null && entry.getTimestamp() > endTime) {
                        continue;
                    }
                    logs.add(entry);
                }
            }
        } catch (IOException e) {
            System.err.println("读取日志文件失败: " + e.getMessage());
        }

        return logs;
    }

    @Override
    public void cleanExpiredLogs(int retentionDays) {
        long cutoffTime = System.currentTimeMillis() - (retentionDays * 24L * 60 * 60 * 1000);
        List<LogEntry> validLogs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = parseLogLine(line);
                if (entry != null && entry.getTimestamp() >= cutoffTime) {
                    validLogs.add(entry);
                }
            }
        } catch (IOException e) {
            System.err.println("读取日志文件失败: " + e.getMessage());
            return;
        }

        // 重写日志文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            for (LogEntry entry : validLogs) {
                writer.write(formatLogEntry(entry));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("清理日志文件失败: " + e.getMessage());
        }
    }

    private void writeLog(String level, String message, Throwable throwable) {
        LogEntry entry = new LogEntry(level, message);
        entry.setThreadName(Thread.currentThread().getName());

        if (throwable != null) {
            entry.setExceptionMessage(throwable.getMessage());
            entry.setStackTrace(getStackTrace(throwable));
        }

        String logLine = formatLogEntry(entry);

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("写入日志失败: " + e.getMessage());
        }

        // 同时输出到控制台
        System.out.println(logLine);
    }

    private String formatLogEntry(LogEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(dateFormat.format(new Date(entry.getTimestamp()))).append("]");
        sb.append(" [").append(entry.getLevel()).append("]");
        sb.append(" [").append(entry.getThreadName()).append("]");
        sb.append(" - ").append(entry.getMessage());

        if (entry.getExceptionMessage() != null) {
            sb.append(" | Exception: ").append(entry.getExceptionMessage());
        }

        return sb.toString();
    }

    private LogEntry parseLogLine(String line) {
        // 简单的日志解析逻辑
        try {
            LogEntry entry = new LogEntry();
            // 解析时间戳、级别、消息等
            // 这里简化处理
            entry.setMessage(line);
            entry.setTimestamp(System.currentTimeMillis());
            return entry;
        } catch (Exception e) {
            return null;
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
