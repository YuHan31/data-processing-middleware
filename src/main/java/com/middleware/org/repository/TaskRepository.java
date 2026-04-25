package com.middleware.org.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.middleware.org.common.TaskStatus;
import com.middleware.org.entity.Task;
import com.middleware.org.model.DataRecord;
import com.middleware.org.model.DataStatistics;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务数据仓库（数据库持久化）
 */
@Repository
public class TaskRepository {

    @Autowired
    private TaskMapper taskMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void save(TaskContext taskContext) {
        Task task = toTask(taskContext);
        Task existing = taskMapper.findByTaskId(taskContext.getTaskId());
        if (existing == null) {
            taskMapper.insert(task);
        } else {
            task.setId(existing.getId());
            taskMapper.updateById(task);
        }
    }

    public TaskContext findById(String taskId) {
        Task task = taskMapper.findByTaskId(taskId);
        return task == null ? null : toTaskContext(task);
    }

    public List<TaskContext> findAll() {
        return taskMapper.findAll().stream()
                .map(this::toTaskContext)
                .collect(Collectors.toList());
    }

    public List<TaskContext> findByStatus(TaskStatus status) {
        return taskMapper.findAll().stream()
                .filter(t -> status.name().equals(t.getStatus()))
                .map(this::toTaskContext)
                .collect(Collectors.toList());
    }

    public List<TaskContext> findByUserId(Long userId) {
        return taskMapper.findByUserId(userId).stream()
                .map(this::toTaskContext)
                .collect(Collectors.toList());
    }

    public void delete(String taskId) {
        taskMapper.deleteByTaskId(taskId);
    }

    public void updateStatus(String taskId, TaskStatus status) {
        taskMapper.updateStatus(taskId, status.name());
    }

    public void updateTimes(String taskId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        taskMapper.updateTimes(taskId, startTime, endTime);
    }

    public boolean exists(String taskId) {
        return taskMapper.findByTaskId(taskId) != null;
    }

    public int count() {
        Long c = taskMapper.selectCount(null);
        return c == null ? 0 : c.intValue();
    }

    public void clear() {
    }

    // ========== TaskContext <-> Task 转换 ==========

    private Task toTask(TaskContext ctx) {
        Task task = new Task();
        task.setTaskId(ctx.getTaskId());
        task.setTaskName(ctx.getTaskName());
        task.setUserId(ctx.getUserId());
        task.setInputFilePath(ctx.getInputFilePath());
        task.setOutputFilePath(ctx.getOutputFilePath());
        task.setFileType(ctx.getFileType());
        task.setOutputFormat(ctx.getOutputFormat());
        task.setStatus(ctx.getStatus() != null ? ctx.getStatus().name() : "UPLOADED");
        task.setOriginalFileName(ctx.getOriginalFileName());
        task.setFileSize(ctx.getFileSize());
        task.setUploadTime(ctx.getUploadTime());
        task.setStartTime(ctx.getStartTime());
        task.setEndTime(ctx.getEndTime());

        // 序列化 ProcessedData 摘要
        if (ctx.getProcessedData() != null) {
            ProcessedData pd = ctx.getProcessedData();
            String json = "{ \"totalCount\": " + pd.getTotalCount()
                    + ", \"validCount\": " + pd.getValidCount()
                    + ", \"invalidCount\": " + pd.getInvalidCount()
                    + ", \"recordCount\": " + pd.getRecordCount() + " }";
            task.setProcessedDataSummary(json);
        }

        // 序列化统计信息
        if (ctx.getStatistics() != null) {
            try {
                task.setStatistics(objectMapper.writeValueAsString(ctx.getStatistics()));
            } catch (Exception ignored) {}
        }

        // 序列化原始数据（用于数据对比）
        if (ctx.getOriginalRecords() != null && !ctx.getOriginalRecords().isEmpty()) {
            try {
                task.setOriginalRecords(objectMapper.writeValueAsString(ctx.getOriginalRecords()));
            } catch (Exception ignored) {}
        }

        // 序列化清洗后数据（用于数据对比）
        if (ctx.getCleanedRecords() != null && !ctx.getCleanedRecords().isEmpty()) {
            try {
                task.setCleanedRecords(objectMapper.writeValueAsString(ctx.getCleanedRecords()));
            } catch (Exception ignored) {}
        }

        return task;
    }

    private TaskContext toTaskContext(Task task) {
        TaskContext ctx = new TaskContext();
        ctx.setTaskId(task.getTaskId());
        ctx.setTaskName(task.getTaskName());
        ctx.setUserId(task.getUserId());
        ctx.setInputFilePath(task.getInputFilePath());
        ctx.setOutputFilePath(task.getOutputFilePath());
        ctx.setFileType(task.getFileType());
        ctx.setOutputFormat(task.getOutputFormat());
        ctx.setStatus(task.getStatus() != null ? TaskStatus.valueOf(task.getStatus()) : TaskStatus.UPLOADED);
        ctx.setOriginalFileName(task.getOriginalFileName());
        ctx.setFileSize(task.getFileSize());
        ctx.setUploadTime(task.getUploadTime());
        ctx.setStartTime(task.getStartTime());
        ctx.setEndTime(task.getEndTime());

        // 反序列化统计信息
        if (task.getStatistics() != null && !task.getStatistics().isBlank()) {
            try {
                ctx.setStatistics(objectMapper.readValue(task.getStatistics(), DataStatistics.class));
            } catch (Exception ignored) {}
        }

        // 反序列化 processedData（包含 recordCount，用于前端显示处理记录数）
        if (task.getProcessedDataSummary() != null && !task.getProcessedDataSummary().isBlank()) {
            try {
                ProcessedData pd = objectMapper.readValue(task.getProcessedDataSummary(), ProcessedData.class);
                ctx.setProcessedData(pd);
            } catch (Exception ignored) {}
        }

        // 反序列化原始数据
        if (task.getOriginalRecords() != null && !task.getOriginalRecords().isBlank()) {
            try {
                List<DataRecord> records = objectMapper.readValue(
                        task.getOriginalRecords(),
                        new TypeReference<List<DataRecord>>() {}
                );
                ctx.setOriginalRecords(records);
            } catch (Exception ignored) {}
        }

        // 反序列化清洗后数据
        if (task.getCleanedRecords() != null && !task.getCleanedRecords().isBlank()) {
            try {
                List<DataRecord> records = objectMapper.readValue(
                        task.getCleanedRecords(),
                        new TypeReference<List<DataRecord>>() {}
                );
                ctx.setCleanedRecords(records);
            } catch (Exception ignored) {}
        }

        return ctx;
    }
}
