package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.model.TaskContext;
import com.middleware.org.config.TaskStartConfig;
import com.middleware.org.common.TaskStatus;
import com.middleware.org.dto.StageDTO;
import com.middleware.org.model.TaskProgress;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.service.DataCompareService;
import com.middleware.org.service.ITaskFlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private DataCompareService dataCompareService;

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
     *
     * 请求体（新增 rules 字段）：
     * {
     *   "outputFormat": "csv",
     *   "enableCleaning": true,
     *   "enableNormalization": false,
     *   "rules": ["TRIM", "REMOVE_NULL", "DESENSITIZE"]
     * }
     */
    @Operation(summary = "启动任务", description = "启动指定的数据处理任务，支持选择清洗规则")
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

            // 传递用户选择的规则列表
            taskService.startTask(
                    taskId,
                    config.getOutputFormat(),
                    config.getOutputPath(),
                    config.getRules()
            );
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
    @Operation(summary = "查询任务进度", description = "查询指定任务的实时执行进度和流程节点")
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
            TaskStatus currentStatus = ctx.getStatus();

            // 构建流程节点
            List<StageDTO> stages = progressService.buildStages(taskId, currentStatus);

            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("progress", progress != null ? progress.getPercentage() : 0);
            data.put("stage", progress != null ? progress.getCurrentStage() : "");
            data.put("message", progress != null ? progress.getMessage() : "");
            data.put("status", currentStatus != null ? currentStatus.name() : "");
            data.put("stages", stages);
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

    /**
     * 数据对比 - 主接口（支持搜索）
     * GET /api/task/compare/{taskId}?page=1&size=20&onlyChanged=false&search=关键词
     *
     * 打开页面时自动加载全部对比，支持关键词搜索字段名或字段值
     */
    @Operation(summary = "数据前后对比", description = "返回原始数据与清洗后数据的对比结果，支持搜索")
    @GetMapping("/compare/{taskId}")
    public Result<Map<String, Object>> compareData(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数，默认100，最大500") @RequestParam(defaultValue = "100") int size,
            @Parameter(description = "是否只返回有变化的数据") @RequestParam(defaultValue = "false") boolean onlyChanged,
            @Parameter(description = "搜索关键词，匹配字段名或字段值") @RequestParam(required = false) String search,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在");
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权访问该任务");
            }

            if (ctx.getStatus() != TaskStatus.FINISHED) {
                return Result.fail("任务尚未完成，无法查看对比");
            }

            // 限制最大条数
            if (size > 500) size = 500;
            if (page < 1) page = 1;

            // 对比数据
            Map<String, Object> result = dataCompareService.compare(
                    ctx.getOriginalRecords(),
                    ctx.getCleanedRecords(),
                    onlyChanged,
                    search,
                    page,
                    size
            );

            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("数据对比失败: " + e.getMessage());
        }
    }

    /**
     * 数据对比 - 统计信息（类似 gitlab 变更统计）
     * GET /api/task/compare/{taskId}/stats
     *
     * 返回变化率、各字段修改次数等统计
     */
    @Operation(summary = "对比统计", description = "返回数据变化的统计信息（变化率、各字段修改次数）")
    @GetMapping("/compare/{taskId}/stats")
    public Result<Map<String, Object>> compareStats(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在");
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权访问该任务");
            }

            Map<String, Object> stats = dataCompareService.buildDiffStats(
                    ctx.getOriginalRecords(),
                    ctx.getCleanedRecords()
            );

            return Result.success(stats);
        } catch (Exception e) {
            return Result.fail("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 按字段查看变化
     * GET /api/task/compare/{taskId}/field/{fieldName}?page=1&size=20
     *
     * 查看某个字段的所有变化，类似 gitlab 按文件过滤
     */
    @Operation(summary = "按字段查看变化", description = "查看指定字段的所有变化记录")
    @GetMapping("/compare/{taskId}/field/{fieldName}")
    public Result<Map<String, Object>> compareByField(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Parameter(description = "字段名") @PathVariable String fieldName,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            TaskContext ctx = taskService.getTaskContext(taskId);
            if (ctx == null) {
                return Result.fail(404, "任务不存在");
            }
            if (!userId.equals(ctx.getUserId())) {
                return Result.fail(403, "无权访问该任务");
            }

            if (size > 100) size = 100;
            if (page < 1) page = 1;

            Map<String, Object> result = dataCompareService.getFieldDiff(
                    ctx.getOriginalRecords(),
                    ctx.getCleanedRecords(),
                    fieldName,
                    page,
                    size
            );

            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取字段变化失败: " + e.getMessage());
        }
    }
}