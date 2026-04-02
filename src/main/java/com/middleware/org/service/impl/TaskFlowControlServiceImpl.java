package com.middleware.org.service.impl;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.common.TaskStatus;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.repository.TaskRepository;
import com.middleware.org.service.ITaskFlowControlService;
import com.middleware.org.task.TaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
    public void startTask(String taskId, String outputFormat, String outputPath, boolean enableCleaning, boolean enableNormalization) {
        TaskContext taskContext = taskRepository.findById(taskId);
        if (taskContext == null) {
            throw new RuntimeException("任务不存在: " + taskId);
        }

        // 更新任务配置
        if (outputFormat != null && !outputFormat.trim().isEmpty()) {
            taskContext.setOutputFormat(outputFormat);
            // 同步更新已有路径的扩展名
            String currentPath = taskContext.getOutputFilePath();
            if (currentPath != null && (outputPath == null || outputPath.trim().isEmpty())) {
                int lastDot = currentPath.lastIndexOf('.');
                if (lastDot > 0) {
                    taskContext.setOutputFilePath(currentPath.substring(0, lastDot + 1) + outputFormat);
                }
            }
        }

        if (outputPath != null && !outputPath.trim().isEmpty()) {
            File f = new File(outputPath);
            String finalPath;
            if (f.isDirectory()) {
                // 用户输入的是文件夹路径，自动生成文件名
                String ext = (outputFormat != null && !outputFormat.trim().isEmpty()) ? outputFormat : "csv";
                String fileName = taskId + "_output." + ext;
                finalPath = outputPath + File.separator + fileName;
            } else {
                // 用户输入的是文件路径，如果选了格式，替换扩展名
                if (outputFormat != null && !outputFormat.trim().isEmpty()) {
                    int lastDot = outputPath.lastIndexOf('.');
                    if (lastDot > 0) {
                        finalPath = outputPath.substring(0, lastDot + 1) + outputFormat;
                    } else {
                        finalPath = outputPath + "." + outputFormat;
                    }
                } else {
                    finalPath = outputPath;
                }
            }
            taskContext.setOutputFilePath(finalPath);
        }

        taskContext.setEnableCleaning(enableCleaning);
        taskContext.setEnableNormalization(enableNormalization);

        taskRepository.save(taskContext);
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
    public Map<String, Object> listTasksByUserId(Long userId, int page, int size) {
        List<TaskResult> all = taskRepository.findByUserId(userId).stream()
                .map(this::convertToTaskResult)
                .collect(Collectors.toList());
        int total = all.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<TaskResult> pageData = fromIndex < total ? all.subList(fromIndex, toIndex) : List.of();

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

        // 如果任务已经完成或失败，不允许终止
        if (taskContext.getStatus() == TaskStatus.FINISHED ||
            taskContext.getStatus() == TaskStatus.FAILED) {
            return false;
        }

        taskContext.setStatus(TaskStatus.FAILED);
        taskRepository.save(taskContext);
        return true;
    }

    @Override
    public boolean deleteTask(String taskId) {
        if (!taskRepository.exists(taskId)) {
            return false;
        }
        taskRepository.delete(taskId);
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

        if (taskContext.getStartTime() != null) {
            result.setStartTime(java.sql.Timestamp.valueOf(taskContext.getStartTime()).getTime());
        }
        if (taskContext.getEndTime() != null) {
            result.setEndTime(java.sql.Timestamp.valueOf(taskContext.getEndTime()).getTime());
        }

        if (taskContext.getProcessedData() != null) {
            result.setProcessedRecords(taskContext.getProcessedData().getRecordCount());
        }

        return result;
    }
}
