package com.middleware.org.progress;

import com.middleware.org.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进度管理服务
 */
@Service
public class ProgressService {

    private final Map<String, TaskProgress> progressCache = new ConcurrentHashMap<>();

    /**
     * 更新任务进度
     */
    public void updateProgress(String taskId, TaskStatus status) {
        TaskProgress progress = progressCache.computeIfAbsent(taskId, k -> new TaskProgress(taskId, status));
        progress.setStatus(status);
        progress.setCurrentStage(status.name());
        progress.setPercentage(calculatePercentage(status));
        progress.setMessage(getStatusMessage(status));
    }

    /**
     * 更新进度百分比
     */
    public void updateProgress(String taskId, TaskStatus status, int percentage, String message) {
        TaskProgress progress = progressCache.computeIfAbsent(taskId, k -> new TaskProgress(taskId, status));
        progress.setStatus(status);
        progress.setPercentage(percentage);
        progress.setMessage(message);
    }

    /**
     * 获取任务进度
     */
    public TaskProgress getProgress(String taskId) {
        return progressCache.get(taskId);
    }

    /**
     * 清除任务进度
     */
    public void clearProgress(String taskId) {
        progressCache.remove(taskId);
    }

    /**
     * 计算进度百分比
     */
    private int calculatePercentage(TaskStatus status) {
        switch (status) {
            case UPLOADED:
                return 10;
            case PARSING:
                return 30;
            case CLEANING:
                return 50;
            case NORMALIZING:
                return 70;
            case EXPORTING:
                return 90;
            case FINISHED:
                return 100;
            case FAILED:
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 获取状态消息
     */
    private String getStatusMessage(TaskStatus status) {
        switch (status) {
            case UPLOADED:
                return "文件已上传";
            case PARSING:
                return "正在解析数据";
            case CLEANING:
                return "正在清洗数据";
            case NORMALIZING:
                return "正在标准化数据";
            case EXPORTING:
                return "正在导出数据";
            case FINISHED:
                return "任务完成";
            case FAILED:
                return "任务失败";
            default:
                return "未知状态";
        }
    }
}
