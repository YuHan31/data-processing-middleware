package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.model.TaskContext;
import com.middleware.org.config.TaskStartConfig;
import com.middleware.org.common.TaskStatus;
import com.middleware.org.model.TaskProgress;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.service.ITaskFlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理控制器
 */
@Tag(name = "任务管理", description = "任务创建、启动、状态查询等接口")
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private ITaskFlowControlService taskService;

    @Autowired
    private ProgressService progressService;

    /**
     * 创建任务
     * POST /api/task/create
     */
    @Operation(summary = "创建任务", description = "创建一个新的数据处理任务")
    @PostMapping("/create")
    public Result<Map<String, Object>> createTask(@RequestBody TaskContext taskContext, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }
            taskContext.setUserId(userId);
            String taskId = taskService.createTask(taskContext);
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            return Result.success("任务创建成功", data);
        } catch (Exception e) {
            return Result.fail("任务创建失败: " + e.getMessage());
        }
    }

    /**
     * 启动任务
     * POST /api/task/start/{taskId}
     */
    @Operation(summary = "启动任务", description = "启动指定的数据处理任务")
    @PostMapping("/start/{taskId}")
    public Result<Void> startTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @RequestBody TaskStartConfig config,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在: " + taskId);
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权操作该任务");
            }

            taskService.startTask(taskId, config.getOutputFormat(), config.getOutputPath(),
                    config.isEnableCleaning(), config.isEnableNormalization());
            return Result.success("任务启动成功");
        } catch (Exception e) {
            return Result.fail("任务启动失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务状态
     * GET /api/task/status/{taskId}
     */
    @Operation(summary = "查询任务状态", description = "查询指定任务的执行状态和进度")
    @GetMapping("/status/{taskId}")
    public Result<Map<String, Object>> getTaskStatus(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在: " + taskId);
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权访问该任务");
            }

            TaskStatus status = taskService.getTaskStatus(taskId);
            TaskProgress progress = progressService.getProgress(taskId);

            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("progress", progress);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的任务列表（支持分页）
     * GET /api/task/list?page=1&size=10
     */
    @Operation(summary = "获取任务列表", description = "分页获取当前用户的任务列表，page从1开始，默认每页10条")
    @GetMapping("/list")
    public Result<Map<String, Object>> listTasks(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }
            Map<String, Object> data = taskService.listTasksByUserId(userId, page, size);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务进度
     * GET /api/task/progress/{taskId}
     */
    @Operation(summary = "查询任务进度", description = "查询指定任务的实时执行进度")
    @GetMapping("/progress/{taskId}")
    public Result<Map<String, Object>> getTaskProgress(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在或进度信息未初始化");
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权访问该任务");
            }

            TaskProgress progress = progressService.getProgress(taskId);
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("progress", progress != null ? progress.getPercentage() : 0);
            data.put("stage", progress != null ? progress.getCurrentStage() : "");
            data.put("message", progress != null ? progress.getMessage() : "");
            data.put("status", progress != null ? progress.getStatus().name() : "");
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取任务进度失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务
     * DELETE /api/task/{taskId}
     */
    @Operation(summary = "删除任务", description = "删除指定的任务")
    @DeleteMapping("/{taskId}")
    public Result<Void> deleteTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在: " + taskId);
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权操作该任务");
            }

            taskService.deleteTask(taskId);
            return Result.success("任务删除成功");
        } catch (Exception e) {
            return Result.fail("删除任务失败: " + e.getMessage());
        }
    }

    /**
     * 停止任务
     * POST /api/task/stop/{taskId}
     */
    @Operation(summary = "停止任务", description = "停止正在执行的任务")
    @PostMapping("/stop/{taskId}")
    public Result<Void> stopTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在: " + taskId);
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权操作该任务");
            }

            boolean success = taskService.stopTask(taskId);
            if (success) {
                return Result.success("任务已停止");
            } else {
                return Result.fail("任务已完成或失败，无法终止");
            }
        } catch (Exception e) {
            return Result.fail("停止任务失败: " + e.getMessage());
        }
    }
}
