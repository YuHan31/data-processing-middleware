package com.middleware.org.service.impl;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.model.TaskStatus;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.repository.TaskRepository;
import com.middleware.org.service.ITaskFlowControlService;
import com.middleware.org.task.TaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务流程控制服务实现
 */
@Service
public class TaskFlowControlServiceImpl implements ITaskFlowControlService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ProgressService progressService;

    @Override
    public String createTask(TaskContext taskContext) {
        String taskId = generateTaskId();
        taskContext.setTaskId(taskId);
        taskContext.setStatus(TaskStatus.UPLOADED);

        taskRepository.save(taskContext);
        progressService.updateProgress(taskId, TaskStatus.UPLOADED);

        return taskId;
    }

    @Override
    public void startTask(String taskId) {
        TaskContext taskContext = taskRepository.findById(taskId);
        if (taskContext == null) {
            throw new RuntimeException("任务不存在: " + taskId);
        }

        taskExecutor.executeTask(taskContext);
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        TaskContext taskContext = taskRepository.findById(taskId);
        if (taskContext == null) {
            throw new RuntimeException("任务不存在: " + taskId);
        }
        return taskContext.getStatus();
    }

    @Override
    public Map<String, Object> listTasksByPage(int page, int size) {
        List<TaskResult> all = taskRepository.findAll().stream()
                .map(this::convertToTaskResult)
                .collect(Collectors.toList());
        int total = all.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<TaskResult> pageData = all.subList(fromIndex, toIndex);

        Map<String, Object> result = new HashMap<>();
        result.put("tasks", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        return result;
    }

    @Override
    public TaskContext getTaskContext(String taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public boolean stopTask(String taskId) {
        TaskContext taskContext = taskRepository.findById(taskId);
        if (taskContext == null) {
            return false;
        }

        taskContext.setStatus(TaskStatus.FAILED);
        taskRepository.save(taskContext);
        return true;
    }

    private String generateTaskId() {
        return "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TaskResult convertToTaskResult(TaskContext taskContext) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskContext.getTaskId());
        result.setTaskName(taskContext.getTaskName());
        result.setStatus(taskContext.getStatus().name());
        result.setInputFilePath(taskContext.getInputFilePath());
        result.setOutputFilePath(taskContext.getOutputFilePath());
        return result;
    }
}
