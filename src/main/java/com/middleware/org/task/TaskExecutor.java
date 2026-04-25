package com.middleware.org.task;

import com.middleware.org.common.TaskStatus;
import com.middleware.org.model.DataRecord;
import com.middleware.org.model.TaskContext;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.repository.TaskRepository;
import com.middleware.org.service.*;
import com.middleware.org.statistics.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务执行器
 */
@Component
public class TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    @Autowired
    private IDataParseService dataParseService;

    @Autowired
    private IDataCleanService dataCleanService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private IDataOutputService dataOutputService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ILogService logService;

    @Autowired
    private TaskRepository taskRepository;

    @Async("asyncTaskExecutor")
    public void executeTask(TaskContext taskContext) {
        String taskId = taskContext.getTaskId();

        try {
            taskContext.setStartTime(LocalDateTime.now());
            taskRepository.updateTimes(taskId, taskContext.getStartTime(), null);
            logService.info(taskId, "PARSING", "任务开始执行");

            // 1. 数据解析阶段
            updateTaskStatus(taskContext, TaskStatus.PARSING);
            dataParseService.parse(taskContext);
            logService.info(taskId, "PARSING", "数据解析完成，共 " + taskContext.getProcessedData().getRecordCount() + " 条记录");

            // 保存原始数据（深拷贝）
            taskContext.setOriginalRecords(deepCopyRecords(taskContext.getProcessedData().getRecords()));

            // 2. 数据清洗阶段
            updateTaskStatus(taskContext, TaskStatus.CLEANING);
            dataCleanService.clean(taskContext);
            logService.info(taskId, "CLEANING", "数据清洗完成");

            // 保存清洗后数据
            taskContext.setCleanedRecords(new ArrayList<>(taskContext.getProcessedData().getRecords()));

            // 持久化 processedData / originalRecords / cleanedRecords 到数据库
            taskRepository.save(taskContext);

            // 3. 数据统计阶段
            statisticsService.generateStatistics(taskContext);
            logService.info(taskId, "STATISTICS", "数据统计完成");

            // 4. 数据导出阶段
            updateTaskStatus(taskContext, TaskStatus.EXPORTING);
            dataOutputService.export(taskContext);
            logService.info(taskId, "EXPORTING", "数据导出完成");

            // 5. 任务完成
            taskContext.setEndTime(LocalDateTime.now());
            taskRepository.updateTimes(taskId, taskContext.getStartTime(), taskContext.getEndTime());
            updateTaskStatus(taskContext, TaskStatus.FINISHED);
            logService.info(taskId, "FINISHED", "任务执行成功");

        } catch (Exception e) {
            taskContext.setEndTime(LocalDateTime.now());
            taskRepository.updateTimes(taskId, taskContext.getStartTime(), taskContext.getEndTime());
            updateTaskStatus(taskContext, TaskStatus.FAILED);

            logService.error(taskId, "ERROR",
                    "任务执行异常: " + e.getClass().getSimpleName() + " - " + e.getMessage(),
                    "任务执行失败：" + parseUserMessage(e));

            throw new RuntimeException("任务执行失败", e);
        }
    }

    private void updateTaskStatus(TaskContext taskContext, TaskStatus status) {
        taskContext.setStatus(status);
        taskRepository.updateStatus(taskContext.getTaskId(), status);
        progressService.updateProgress(taskContext.getTaskId(), status);
    }

    private String parseUserMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) return "未知错误";

        if (msg.contains("FileNotFoundException")) return "文件未找到，请检查文件路径";
        if (msg.contains("ParseException") || msg.contains("解析")) return "数据格式解析失败，请检查文件格式";
        if (msg.contains("EmptyFile") || msg.contains("空文件")) return "文件为空";
        if (msg.contains("JsonProcessingException") || msg.contains("JSON")) return "JSON格式错误";
        if (msg.contains("IOException")) return "文件读写错误";

        return "任务执行失败：" + msg;
    }

    private List<DataRecord> deepCopyRecords(List<DataRecord> original) {
        if (original == null) return new ArrayList<>();
        return new ArrayList<>(original);
    }
}