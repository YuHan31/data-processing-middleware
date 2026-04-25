package com.middleware.org.service;

import com.middleware.org.dto.request.AddCleanRuleRequest;
import com.middleware.org.entity.CleanRule;
import com.middleware.org.entity.Task;
import com.middleware.org.entity.User;
import com.middleware.org.model.LogEntry;

import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 */
public interface IAdminService {

    // ========== 用户管理 ==========

    /**
     * 获取所有用户列表（分页）
     */
    Map<String, Object> listUsers(int page, int size, String keyword);

    /**
     * 启用/禁用用户
     */
    void toggleUserEnabled(Long userId);

    /**
     * 根据用户ID获取用户详情
     */
    User getUserById(Long userId);

    // ========== 任务管理 ==========

    /**
     * 获取所有用户的任务列表（分页）
     */
    Map<String, Object> listAllTasks(int page, int size, Long userId, String status);

    /**
     * 获取指定用户的任务列表
     */
    Map<String, Object> listTasksByUserId(Long userId, int page, int size);

    /**
     * 获取任务关联的清洗规则
     */
    List<String> getTaskCleanRules(String taskId);

    // ========== 日志管理 ==========

    /**
     * 获取指定任务的完整日志
     */
    List<LogEntry> getTaskLogs(String taskId);

    /**
     * 获取指定用户的所有日志
     */
    List<LogEntry> getUserLogs(Long userId);

    /**
     * 获取所有日志
     */
    List<LogEntry> getAllLogs();

    // ========== 清洗规则管理 ==========

    /**
     * 获取所有清洗规则（含禁用状态）
     */
    List<CleanRule> listAllRules();

    /**
     * 添加清洗规则
     */
    CleanRule addCleanRule(AddCleanRuleRequest request);

    /**
     * 删除清洗规则
     */
    void deleteCleanRule(Long id);

    /**
     * 更新清洗规则
     */
    void updateCleanRule(Long id, AddCleanRuleRequest request);

    /**
     * 切换规则启用状态
     */
    void toggleRuleEnabled(Long id);
}
