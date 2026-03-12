package com.middleware.org.controller;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.service.ITaskFlowControlService;
import com.middleware.org.service.ServiceFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务管理控制器
 * 提供任务相关的REST API接口
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ITaskFlowControlService taskService;

    public TaskController() {
        this.taskService = ServiceFactory.getInstance().getTaskFlowControlService();
    }

    /**
     * 启动新任务
     * POST /api/tasks
     */
    @PostMapping
    public Map<String, Object> startTask(@RequestBody TaskContext taskContext) {
        Map<String, Object> response = new HashMap<>();
        try {
            String taskId = taskService.startTask(taskContext);
            response.put("success", true);
            response.put("taskId", taskId);
            response.put("message", "任务启动成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "任务启动失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 获取所有任务列表
     * GET /api/tasks
     */
    @GetMapping
    public Map<String, Object> listTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TaskResult> tasks = taskService.listAllTasks();
            response.put("success", true);
            response.put("data", tasks);
            response.put("total", tasks.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取任务列表失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 获取指定任务状态
     * GET /api/tasks/{taskId}
     */
    @GetMapping("/{taskId}")
    public Map<String, Object> getTaskStatus(@PathVariable String taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            TaskResult taskResult = taskService.getTaskStatus(taskId);
            if (taskResult != null) {
                response.put("success", true);
                response.put("data", taskResult);
            } else {
                response.put("success", false);
                response.put("message", "任务不存在");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取任务状态失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 停止指定任务
     * DELETE /api/tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public Map<String, Object> stopTask(@PathVariable String taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = taskService.stopTask(taskId);
            response.put("success", success);
            response.put("message", success ? "任务已停止" : "停止任务失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "停止任务失败: " + e.getMessage());
        }
        return response;
    }
}
