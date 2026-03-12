package com.middleware.org.repository;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 任务数据仓库
 */
@Repository
public class TaskRepository {

    private final Map<String, TaskContext> taskStore = new ConcurrentHashMap<>();

    /**
     * 保存任务
     */
    public void save(TaskContext taskContext) {
        taskStore.put(taskContext.getTaskId(), taskContext);
    }

    /**
     * 根据ID查找任务
     */
    public TaskContext findById(String taskId) {
        return taskStore.get(taskId);
    }

    /**
     * 查找所有任务
     */
    public List<TaskContext> findAll() {
        return new ArrayList<>(taskStore.values());
    }

    /**
     * 根据状态查找任务
     */
    public List<TaskContext> findByStatus(TaskStatus status) {
        return taskStore.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * 删除任务
     */
    public void delete(String taskId) {
        taskStore.remove(taskId);
    }

    /**
     * 更新任务状态
     */
    public void updateStatus(String taskId, TaskStatus status) {
        TaskContext task = taskStore.get(taskId);
        if (task != null) {
            task.setStatus(status);
        }
    }

    /**
     * 检查任务是否存在
     */
    public boolean exists(String taskId) {
        return taskStore.containsKey(taskId);
    }

    /**
     * 获取任务总数
     */
    public int count() {
        return taskStore.size();
    }

    /**
     * 清空所有任务
     */
    public void clear() {
        taskStore.clear();
    }
}
