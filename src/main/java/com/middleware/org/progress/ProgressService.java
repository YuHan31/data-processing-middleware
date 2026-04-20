package com.middleware.org.progress;

import com.middleware.org.common.TaskStatus;
import com.middleware.org.dto.StageDTO;
import com.middleware.org.model.TaskProgress;
import com.middleware.org.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 进度管理服务
 */
@Service
public class ProgressService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * 更新任务进度（写入数据库）
     */
    public void updateProgress(String taskId, TaskStatus status) {
        taskRepository.updateStatus(taskId, status);
    }

    public void updateProgress(String taskId, TaskStatus status, int percentage, String message) {
        taskRepository.updateStatus(taskId, status);
    }

    /**
     * 从数据库读取进度
     */
    public TaskProgress getProgress(String taskId) {
        var taskContext = taskRepository.findById(taskId);
        if (taskContext == null) {
            return null;
        }
        TaskStatus status = taskContext.getStatus();
        TaskProgress progress = new TaskProgress(taskId, status);
        progress.setPercentage(calculatePercentage(status));
        progress.setCurrentStage(getStageName(status));
        progress.setMessage(getStatusMessage(status));
        return progress;
    }

    public void clearProgress(String taskId) {
        // 无需清理缓存，直接删除
    }

    /**
     * 构建流程节点列表
     * 用于前端绘制流程图
     */
    public List<StageDTO> buildStages(String taskId, TaskStatus currentStatus) {
        // 流程节点定义（按执行顺序）
        LinkedHashMap<String, String> stageDefinitions = new LinkedHashMap<>();
        stageDefinitions.put("UPLOADED", "文件上传");
        stageDefinitions.put("PARSING", "数据解析");
        stageDefinitions.put("CLEANING", "数据清洗");
        stageDefinitions.put("EXPORTING", "数据导出");
        stageDefinitions.put("FINISHED", "任务完成");

        List<StageDTO> stages = new ArrayList<>();
        String currentStageName = currentStatus != null ? currentStatus.name() : null;

        // 是否已完成（成功或失败）
        boolean isFinished = currentStatus == TaskStatus.FINISHED || currentStatus == TaskStatus.FAILED;
        // 是否失败
        boolean isFailed = currentStatus == TaskStatus.FAILED;

        for (Map.Entry<String, String> entry : stageDefinitions.entrySet()) {
            String stageCode = entry.getKey();
            String stageName = entry.getValue();

            StageDTO dto = new StageDTO(stageCode, stageName, "WAIT");

            // 判断状态
            if (isFailed) {
                if (stageCode.equals(currentStageName)) {
                    dto.setStatus("FAILED");
                } else if (isStageBefore(stageCode, currentStageName, stageDefinitions.keySet())) {
                    dto.setStatus("DONE");
                }
            } else if (isFinished) {
                dto.setStatus("DONE");
            } else if (stageCode.equals(currentStageName)) {
                dto.setStatus("RUNNING");
            } else if (isStageBefore(stageCode, currentStageName, stageDefinitions.keySet())) {
                dto.setStatus("DONE");
            }

            stages.add(dto);
        }

        return stages;
    }

    /**
     * 判断 stageA 是否在 stageB 之前
     */
    private boolean isStageBefore(String stageA, String stageB, Set<String> orderedStages) {
        if (stageB == null) return false;
        List<String> order = new ArrayList<>(orderedStages);
        int idxA = order.indexOf(stageA);
        int idxB = order.indexOf(stageB);
        return idxA >= 0 && idxB >= 0 && idxA < idxB;
    }

    private int calculatePercentage(TaskStatus status) {
        switch (status) {
            case UPLOADED: return 10;
            case PARSING: return 30;
            case CLEANING: return 50;
            case EXPORTING: return 90;
            case FINISHED: return 100;
            case FAILED: return 0;
            default: return 0;
        }
    }

    private String getStageName(TaskStatus status) {
        switch (status) {
            case UPLOADED: return "文件上传";
            case PARSING: return "数据解析";
            case CLEANING: return "数据清洗";
            case EXPORTING: return "数据导出";
            case FINISHED: return "任务完成";
            case FAILED: return "任务失败";
            default: return "未知";
        }
    }

    private String getStatusMessage(TaskStatus status) {
        switch (status) {
            case UPLOADED: return "文件已上传";
            case PARSING: return "正在解析数据";
            case CLEANING: return "正在清洗数据";
            case EXPORTING: return "正在导出数据";
            case FINISHED: return "任务完成";
            case FAILED: return "任务失败";
            default: return "未知状态";
        }
    }
}