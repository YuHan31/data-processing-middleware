package com.middleware.org.controller;

import com.middleware.org.model.LogEntry;
import com.middleware.org.service.ILogService;
import com.middleware.org.service.ServiceFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志管理控制器
 * 提供日志查询相关的REST API接口
 */
@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final ILogService logService;

    public LogController() {
        this.logService = ServiceFactory.getInstance().getLogService();
    }

    /**
     * 查询日志
     * GET /api/logs
     */
    @GetMapping
    public Map<String, Object> queryLogs(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String level) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LogEntry> logs = logService.queryLogs(startTime, endTime, level);
            response.put("success", true);
            response.put("data", logs);
            response.put("total", logs.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询日志失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 清理过期日志
     * DELETE /api/logs/expired
     */
    @DeleteMapping("/expired")
    public Map<String, Object> cleanExpiredLogs(@RequestParam(defaultValue = "30") int retentionDays) {
        Map<String, Object> response = new HashMap<>();
        try {
            logService.cleanExpiredLogs(retentionDays);
            response.put("success", true);
            response.put("message", "清理过期日志成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "清理日志失败: " + e.getMessage());
        }
        return response;
    }
}
