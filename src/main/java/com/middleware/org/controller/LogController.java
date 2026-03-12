package com.middleware.org.controller;

import com.middleware.org.common.ApiResponse;
import com.middleware.org.model.LogEntry;
import com.middleware.org.service.ILogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志管理控制器
 */
@Tag(name = "日志管理", description = "任务日志查询接口")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private ILogService logService;

    /**
     * 查询任务日志
     * GET /api/log/{taskId}
     */
    @Operation(summary = "查询任务日志", description = "查询指定任务的执行日志")
    @GetMapping("/{taskId}")
    public ApiResponse<Map<String, Object>> queryLogs(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        try {
            List<LogEntry> logs = logService.queryLogs(taskId);
            Map<String, Object> data = new HashMap<>();
            data.put("logs", logs);
            data.put("total", logs.size());
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("查询日志失败: " + e.getMessage());
        }
    }
}
