package com.middleware.org.controller;

import com.middleware.org.common.ApiResponse;
import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskStatus;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.progress.TaskProgress;
import com.middleware.org.service.ITaskFlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Map<String, Object>> createTask(@RequestBody TaskContext taskContext) {
        try {
            String taskId = taskService.createTask(taskContext);
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            return ApiResponse.success("任务创建成功", data);
        } catch (Exception e) {
            return ApiResponse.error("任务创建失败: " + e.getMessage());
        }
    }

    /**
     * 启动任务
     * POST /api/task/start/{taskId}
     */
    @Operation(summary = "启动任务", description = "启动指定的数据处理任务")
    @PostMapping("/start/{taskId}")
    public ApiResponse<Void> startTask(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        try {
            taskService.startTask(taskId);
            return ApiResponse.success("任务启动成功", null);
        } catch (Exception e) {
            return ApiResponse.error("任务启动失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务状态
     * GET /api/task/status/{taskId}
     */
    @Operation(summary = "查询任务状态", description = "查询指定任务的执行状态和进度")
    @GetMapping("/status/{taskId}")
    public ApiResponse<Map<String, Object>> getTaskStatus(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        try {
            TaskStatus status = taskService.getTaskStatus(taskId);
            TaskProgress progress = progressService.getProgress(taskId);

            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("progress", progress);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有任务列表（支持分页）
     * GET /api/task/list?page=1&size=10
     */
    @Operation(summary = "获取任务列表", description = "分页获取任务列表，page从1开始，默认每页10条")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> listTasks(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> data = taskService.listTasksByPage(page, size);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务进度
     * GET /api/task/progress/{taskId}
     */
    @Operation(summary = "查询任务进度", description = "查询指定任务的实时执行进度")
    @GetMapping("/progress/{taskId}")
    public ApiResponse<Map<String, Object>> getTaskProgress(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        try {
            TaskProgress progress = progressService.getProgress(taskId);
            if (progress == null) {
                return ApiResponse.error(404, "任务不存在或进度信息未初始化");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", progress.getTaskId());
            data.put("progress", progress.getPercentage());
            data.put("stage", progress.getCurrentStage());
            data.put("message", progress.getMessage());
            data.put("status", progress.getStatus());
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取任务进度失败: " + e.getMessage());
        }
    }

    /**
     * 停止任务
     * POST /api/task/stop/{taskId}
     */
    @Operation(summary = "停止任务", description = "停止正在执行的任务")
    @PostMapping("/stop/{taskId}")
    public ApiResponse<Void> stopTask(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        try {
            boolean success = taskService.stopTask(taskId);
            if (success) {
                return ApiResponse.success("任务已停止", null);
            } else {
                return ApiResponse.error("停止任务失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("停止任务失败: " + e.getMessage());
        }
    }
}
