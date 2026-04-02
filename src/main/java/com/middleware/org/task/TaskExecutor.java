package com.middleware.org.task;

import com.middleware.org.model.TaskContext;
import com.middleware.org.common.TaskStatus;
import com.middleware.org.progress.ProgressService;
import com.middleware.org.repository.TaskRepository;
import com.middleware.org.service.IDataCleanService;
import com.middleware.org.service.IDataOutputService;
import com.middleware.org.service.IDataParseService;
import com.middleware.org.service.ILogService;
import com.middleware.org.statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 任务执行器
 */
@Component
public class TaskExecutor {

    @Autowired
    private IDataParseService dataParseService;

    @Autowired
    private IDataCleanService dataCleanService;

    @Autowired
    private IDataOutputService dataOutputService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ILogService logService;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * 异步执行任务
     */
    @Async("asyncTaskExecutor")
    public void executeTask(TaskContext taskContext) {
        String taskId = taskContext.getTaskId();

        try {
            taskContext.setStartTime(LocalDateTime.now());
            logService.info(taskId, "任务开始执行");

            // 1. 数据解析阶段
            updateTaskStatus(taskContext, TaskStatus.PARSING);
            dataParseService.parse(taskContext);
            logService.info(taskId, "数据解析完成");

            // 2. 数据清洗阶段
            if (taskContext.isEnableCleaning()) {
                updateTaskStatus(taskContext, TaskStatus.CLEANING);
                dataCleanService.clean(taskContext);
                logService.info(taskId, "数据清洗完成");
            }

            // 3. 数据标准化阶段
            if (taskContext.isEnableNormalization()) {
                updateTaskStatus(taskContext, TaskStatus.NORMALIZING);
                dataCleanService.normalize(taskContext);
                logService.info(taskId, "数据标准化完成");
            }

            // 4. 数据统计阶段
            statisticsService.generateStatistics(taskContext);
            logService.info(taskId, "数据统计完成");

            // 5. 数据导出阶段
            updateTaskStatus(taskContext, TaskStatus.EXPORTING);
            dataOutputService.export(taskContext);
            logService.info(taskId, "数据导出完成");

            // 6. 任务完成
            taskContext.setEndTime(LocalDateTime.now());
            updateTaskStatus(taskContext, TaskStatus.FINISHED);
            logService.info(taskId, "任务执行成功");

        } catch (Exception e) {
            taskContext.setEndTime(LocalDateTime.now());
            updateTaskStatus(taskContext, TaskStatus.FAILED);
            logService.error(taskId, "任务执行失败: " + e.getMessage());
            throw new RuntimeException("任务执行失败", e);
        }
    }

    private void updateTaskStatus(TaskContext taskContext, TaskStatus status) {
        taskContext.setStatus(status);
        taskRepository.save(taskContext);
        progressService.updateProgress(taskContext.getTaskId(), status);
    }
}
