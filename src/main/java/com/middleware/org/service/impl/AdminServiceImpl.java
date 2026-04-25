package com.middleware.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.middleware.org.dto.request.AddCleanRuleRequest;
import com.middleware.org.entity.CleanRule;
import com.middleware.org.entity.SystemLog;
import com.middleware.org.entity.Task;
import com.middleware.org.entity.TaskCleanRule;
import com.middleware.org.entity.User;
import com.middleware.org.model.LogEntry;
import com.middleware.org.repository.CleanRuleRepository;
import com.middleware.org.repository.SystemLogRepository;
import com.middleware.org.repository.TaskCleanRuleRepository;
import com.middleware.org.repository.TaskMapper;
import com.middleware.org.repository.TaskRepository;
import com.middleware.org.repository.UserRepository;
import com.middleware.org.service.IAdminService;
import com.middleware.org.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员服务实现
 */
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private CleanRuleRepository cleanRuleRepository;

    @Autowired
    private TaskCleanRuleRepository taskCleanRuleRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private ILogService logService;

    // ========== 用户管理 ==========

    @Override
    public Map<String, Object> listUsers(int page, int size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getAccount, keyword)
                    .or()
                    .like(User::getName, keyword)
                    .or()
                    .like(User::getEmail, keyword)
                    .or()
                    .like(User::getPhone, keyword);
        }

        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> result = userRepository.selectPage(pageParam, wrapper);

        // 脱敏密码
        List<User> records = result.getRecords().stream()
                .peek(u -> u.setPassword(null))
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return data;
    }

    @Override
    @Transactional
    public void toggleUserEnabled(Long userId) {
        User user = userRepository.selectById(userId);
        if (user != null) {
            user.setEnabled(!user.getEnabled());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.updateById(user);
        }
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    // ========== 任务管理 ==========

    @Override
    public Map<String, Object> listAllTasks(int page, int size, Long userId, String status) {
        Page<Task> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Task::getUserId, userId);
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(Task::getStatus, status);
        }

        wrapper.orderByDesc(Task::getCreateTime);
        IPage<Task> result = taskMapper.selectPage(pageParam, wrapper);

        // 关联用户信息
        Set<Long> userIds = result.getRecords().stream()
                .map(Task::getUserId)
                .filter(u -> u != null)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userRepository.selectList(
                    new LambdaQueryWrapper<User>().in(User::getId, userIds)
            );
            for (User u : users) {
                u.setPassword(null);
                userMap.put(u.getId(), u);
            }
        }

        List<Map<String, Object>> records = result.getRecords().stream()
                .map(task -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", task.getId());
                    map.put("taskId", task.getTaskId());
                    map.put("userId", task.getUserId());
                    map.put("userName", userMap.containsKey(task.getUserId()) ? userMap.get(task.getUserId()).getName() : "");
                    map.put("taskName", task.getTaskName());
                    map.put("status", task.getStatus());
                    map.put("fileType", task.getFileType());
                    map.put("outputFormat", task.getOutputFormat());
                    map.put("fileSize", task.getFileSize());
                    map.put("createTime", task.getCreateTime());
                    map.put("startTime", task.getStartTime());
                    map.put("endTime", task.getEndTime());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return data;
    }

    @Override
    public Map<String, Object> listTasksByUserId(Long userId, int page, int size) {
        Page<Task> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId).orderByDesc(Task::getCreateTime);

        IPage<Task> result = taskMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = result.getRecords().stream()
                .map(task -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("taskId", task.getTaskId());
                    map.put("taskName", task.getTaskName());
                    map.put("status", task.getStatus());
                    map.put("createTime", task.getCreateTime());
                    map.put("startTime", task.getStartTime());
                    map.put("endTime", task.getEndTime());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return data;
    }

    @Override
    public List<String> getTaskCleanRules(String taskId) {
        return taskCleanRuleRepository.findByTaskIdOrderByExecOrder(taskId).stream()
                .map(TaskCleanRule::getRuleCode)
                .collect(Collectors.toList());
    }

    // ========== 日志管理 ==========

    @Override
    public List<LogEntry> getTaskLogs(String taskId) {
        return logService.queryLogs(taskId);
    }

    @Override
    public List<LogEntry> getUserLogs(Long userId) {
        // 获取该用户所有任务的日志
        List<Task> tasks = taskMapper.findByUserId(userId);
        List<LogEntry> allLogs = new ArrayList<>();
        for (Task task : tasks) {
            allLogs.addAll(logService.queryLogs(task.getTaskId()));
        }
        return allLogs;
    }

    @Override
    public List<LogEntry> getAllLogs() {
        // 直接从日志表查询所有日志
        return systemLogRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemLog>()
                        .orderByDesc(SystemLog::getTimestamp)
        ).stream().map(this::toLogEntry).collect(Collectors.toList());
    }

    private LogEntry toLogEntry(SystemLog systemLog) {
        LogEntry entry = new LogEntry();
        entry.setId(systemLog.getId());
        entry.setLevel(systemLog.getLevel());
        entry.setMessage(systemLog.getMessage());
        entry.setUserMessage(systemLog.getUserMessage());
        entry.setTaskId(systemLog.getTaskId());
        entry.setTimestamp(systemLog.getTimestamp());
        entry.setStage(systemLog.getStage());
        entry.setExceptionMessage(systemLog.getExceptionMessage());
        entry.setStackTrace(systemLog.getStackTrace());
        return entry;
    }

    // ========== 清洗规则管理 ==========

    @Override
    public List<CleanRule> listAllRules() {
        return cleanRuleRepository.selectList(
                new LambdaQueryWrapper<CleanRule>()
                        .orderByAsc(CleanRule::getDisplayOrder)
        );
    }

    @Override
    @Transactional
    public CleanRule addCleanRule(AddCleanRuleRequest request) {
        throw new UnsupportedOperationException("不支持通过接口添加清洗规则，新规则需要后端开发者编写Java代码实现");
    }

    @Override
    @Transactional
    public void deleteCleanRule(Long id) {
        cleanRuleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateCleanRule(Long id, AddCleanRuleRequest request) {
        CleanRule rule = cleanRuleRepository.selectById(id);
        if (rule == null) {
            throw new IllegalArgumentException("规则不存在: " + id);
        }

        // 只能修改元数据字段，不能修改 ruleCode、ruleType、level
        if (request.getRuleName() != null) {
            rule.setRuleName(request.getRuleName());
        }
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }
        if (request.getEnabled() != null) {
            rule.setEnabled(request.getEnabled());
        }
        if (request.getDisplayOrder() != null) {
            rule.setDisplayOrder(request.getDisplayOrder());
        }
        rule.setUpdateTime(LocalDateTime.now());
        cleanRuleRepository.updateById(rule);
    }

    @Override
    @Transactional
    public void toggleRuleEnabled(Long id) {
        CleanRule rule = cleanRuleRepository.selectById(id);
        if (rule != null) {
            rule.setEnabled(!rule.getEnabled());
            rule.setUpdateTime(LocalDateTime.now());
            cleanRuleRepository.updateById(rule);
        }
    }
}
