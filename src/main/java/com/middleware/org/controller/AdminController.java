package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.dto.request.AddCleanRuleRequest;
import com.middleware.org.entity.CleanRule;
import com.middleware.org.entity.User;
import com.middleware.org.model.LogEntry;
import com.middleware.org.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Tag(name = "管理员", description = "管理员专属接口 - 用户管理、任务管理、日志管理、规则管理")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    // ========== 管理员权限校验 ==========

    /**
     * 校验当前用户是否为管理员
     */
    private User checkAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        User user = adminService.getUserById(userId);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return null;
        }
        return user;
    }

    // ========== 用户管理 ==========

    /**
     * 获取所有用户列表（分页）
     * GET /api/admin/users?page=1&size=10&keyword=
     */
    @Operation(summary = "获取用户列表", description = "管理员获取所有用户信息，支持分页和关键词搜索")
    @GetMapping("/users")
    public Result<Map<String, Object>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词（账号/姓名/邮箱/手机）") @RequestParam(required = false) String keyword,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            Map<String, Object> data = adminService.listUsers(page, size, keyword);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     * POST /api/admin/users/{userId}/toggle
     */
    @Operation(summary = "启用/禁用用户", description = "切换用户账户的启用状态")
    @PostMapping("/users/{userId}/toggle")
    public Result<Void> toggleUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            adminService.toggleUserEnabled(userId);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.fail("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     * GET /api/admin/users/{userId}
     */
    @Operation(summary = "获取用户详情", description = "获取指定用户的详细信息")
    @GetMapping("/users/{userId}")
    public Result<User> getUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            User user = adminService.getUserById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.fail("获取用户详情失败: " + e.getMessage());
        }
    }

    // ========== 任务管理 ==========

    /**
     * 获取所有用户的任务列表（分页）
     * GET /api/admin/tasks?page=1&size=10&userId=&status=
     */
    @Operation(summary = "获取所有任务", description = "管理员获取所有用户的任务列表，支持按用户和状态筛选")
    @GetMapping("/tasks")
    public Result<Map<String, Object>> listAllTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户ID筛选") @RequestParam(required = false) Long userId,
            @Parameter(description = "任务状态筛选") @RequestParam(required = false) String status,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            Map<String, Object> data = adminService.listAllTasks(page, size, userId, status);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定用户的所有任务
     * GET /api/admin/users/{userId}/tasks?page=1&size=10
     */
    @Operation(summary = "获取用户任务", description = "获取指定用户的所有任务列表")
    @GetMapping("/users/{userId}/tasks")
    public Result<Map<String, Object>> listUserTasks(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            Map<String, Object> data = adminService.listTasksByUserId(userId, page, size);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("获取用户任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务关联的清洗规则
     * GET /api/admin/tasks/{taskId}/rules
     */
    @Operation(summary = "获取任务清洗规则", description = "查看指定任务使用的所有清洗规则")
    @GetMapping("/tasks/{taskId}/rules")
    public Result<List<String>> getTaskRules(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            List<String> rules = adminService.getTaskCleanRules(taskId);
            return Result.success(rules);
        } catch (Exception e) {
            return Result.fail("获取任务规则失败: " + e.getMessage());
        }
    }

    // ========== 日志管理 ==========

    /**
     * 获取指定任务的完整日志
     * GET /api/admin/logs/task/{taskId}
     */
    @Operation(summary = "获取任务日志", description = "查看指定任务的完整执行日志")
    @GetMapping("/logs/task/{taskId}")
    public Result<List<LogEntry>> getTaskLogs(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            List<LogEntry> logs = adminService.getTaskLogs(taskId);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.fail("获取日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定用户的所有日志
     * GET /api/admin/logs/user/{userId}
     */
    @Operation(summary = "获取用户日志", description = "查看指定用户所有任务的日志")
    @GetMapping("/logs/user/{userId}")
    public Result<List<LogEntry>> getUserLogs(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            List<LogEntry> logs = adminService.getUserLogs(userId);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.fail("获取日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有日志
     * GET /api/admin/logs?page=1&size=100
     */
    @Operation(summary = "获取所有日志", description = "管理员查看系统所有日志（谨慎使用）")
    @GetMapping("/logs")
    public Result<List<LogEntry>> getAllLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "100") int size,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            List<LogEntry> logs = adminService.getAllLogs();
            // 简单分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, logs.size());
            List<LogEntry> pageLogs = start < logs.size() ? logs.subList(start, end) : List.of();
            return Result.success(pageLogs);
        } catch (Exception e) {
            return Result.fail("获取日志失败: " + e.getMessage());
        }
    }

    // ========== 清洗规则管理 ==========

    /**
     * 获取所有清洗规则
     * GET /api/admin/clean-rules
     */
    @Operation(summary = "获取清洗规则列表", description = "获取所有清洗规则（含启用状态）")
    @GetMapping("/clean-rules")
    public Result<List<CleanRule>> listRules(HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            List<CleanRule> rules = adminService.listAllRules();
            return Result.success(rules);
        } catch (Exception e) {
            return Result.fail("获取规则列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加清洗规则（已禁用）
     * POST /api/admin/clean-rules
     * 注意：不支持通过接口添加新规则，新规则需要后端开发者编写Java代码实现
     */
    @Operation(summary = "添加清洗规则（已禁用）", description = "此接口已禁用，新规则需要后端开发者编写Java代码实现")
    @PostMapping("/clean-rules")
    public Result<Void> addRule(
            @RequestBody AddCleanRuleRequest request,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        return Result.fail("不支持通过接口添加新规则，新规则需要后端开发者编写Java代码实现");
    }

    /**
     * 更新清洗规则
     * PUT /api/admin/clean-rules/{id}
     */
    @Operation(summary = "更新清洗规则", description = "更新指定清洗规则的配置")
    @PutMapping("/clean-rules/{id}")
    public Result<Void> updateRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @RequestBody AddCleanRuleRequest request,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            adminService.updateCleanRule(id, request);
            return Result.success("规则更新成功");
        } catch (Exception e) {
            return Result.fail("更新规则失败: " + e.getMessage());
        }
    }

    /**
     * 删除清洗规则
     * DELETE /api/admin/clean-rules/{id}
     */
    @Operation(summary = "删除清洗规则", description = "删除指定的数据清洗规则")
    @DeleteMapping("/clean-rules/{id}")
    public Result<Void> deleteRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            adminService.deleteCleanRule(id);
            return Result.success("规则删除成功");
        } catch (Exception e) {
            return Result.fail("删除规则失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用规则
     * POST /api/admin/clean-rules/{id}/toggle
     */
    @Operation(summary = "启用/禁用规则", description = "切换清洗规则的启用状态")
    @PostMapping("/clean-rules/{id}/toggle")
    public Result<Void> toggleRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            HttpSession session) {
        User admin = checkAdmin(session);
        if (admin == null) {
            return Result.fail(401, "用户未登录或无管理员权限");
        }

        try {
            adminService.toggleRuleEnabled(id);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.fail("操作失败: " + e.getMessage());
        }
    }
}