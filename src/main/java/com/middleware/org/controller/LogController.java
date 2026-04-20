package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.service.ILogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志管理控制器
 */
@Tag(name = "日志管理", description = "任务失败原因查询")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private ILogService logService;

    /**
     * 获取任务失败原因
     * GET /api/log/{taskId}/reason
     *
     * 仅任务失败时有返回，成功任务返回 null
     */
    @Operation(summary = "获取失败原因", description = "返回任务失败的原因描述（仅失败任务有值）")
    @GetMapping("/{taskId}/reason")
    public Result<Map<String, Object>> getErrorReason(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        String reason = logService.getTaskErrorReason(taskId);
        Map<String, Object> data = new HashMap<>();
        data.put("reason", reason);
        return Result.success(data);
    }
}