package com.middleware.org.task;

import com.middleware.org.model.DataRecord;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Async("asyncTaskExecutor")
    public void executeTask(TaskContext taskContext) {
        String taskId = taskContext.getTaskId();

        try {
            taskContext.setStartTime(LocalDateTime.now());
            logService.info(taskId, "任务开始执行");

            // 1. 数据解析阶段
            updateTaskStatus(taskContext, TaskStatus.PARSING);
            dataParseService.parse(taskContext);
            logService.info(taskId, "数据解析完成，共 " + taskContext.getProcessedData().getRecordCount() + " 条记录");

            // 保存原始数据（深拷贝）
            taskContext.setOriginalRecords(deepCopyRecords(taskContext.getProcessedData().getRecords()));

            // 2. 数据清洗阶段
            updateTaskStatus(taskContext, TaskStatus.CLEANING);
            dataCleanService.clean(taskContext);
            logService.info(taskId, "数据清洗完成");

            // 保存清洗后数据
            taskContext.setCleanedRecords(new ArrayList<>(taskContext.getProcessedData().getRecords()));

            // 3. 数据统计阶段
            statisticsService.generateStatistics(taskContext);
            logService.info(taskId, "数据统计完成");

            // 4. 数据导出阶段
            updateTaskStatus(taskContext, TaskStatus.EXPORTING);
            dataOutputService.export(taskContext);
            logService.info(taskId, "数据导出完成");

            // 5. 任务完成
            taskContext.setEndTime(LocalDateTime.now());
            updateTaskStatus(taskContext, TaskStatus.FINISHED);
            logService.info(taskId, "任务执行成功");

        } catch (Exception e) {
            taskContext.setEndTime(LocalDateTime.now());
            updateTaskStatus(taskContext, TaskStatus.FAILED);

            logService.error(taskId,
                    "任务执行异常: " + e.getClass().getSimpleName() + " - " + e.getMessage(),
                    "任务执行失败：" + parseUserMessage(e));

            throw new RuntimeException("任务执行失败", e);
        }
    }

    /**
     * 深拷贝 DataRecord 列表（保留原始数据用于对比）
     */
    private List<DataRecord> deepCopyRecords(List<DataRecord> records) {
        List<DataRecord> copy = new ArrayList<>();
        for (DataRecord record : records) {
            DataRecord newRecord = new DataRecord();
            newRecord.setRecordId(record.getRecordId());
            newRecord.setValid(record.isValid());
            newRecord.setSourceType(record.getSourceType());
            // 深拷贝字段
            Map<String, Object> fieldsCopy = new java.util.HashMap<>();
            if (record.getFields() != null) {
                for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
                    fieldsCopy.put(entry.getKey(), entry.getValue());
                }
            }
            newRecord.setFields(fieldsCopy);
            copy.add(newRecord);
        }
        return copy;
    }

    /**
     * 解析异常的友好提示
     */
    private String parseUserMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            return "发生了未知错误，请联系管理员";
        }
        if (msg.contains("FileNotFound")) {
            return "数据文件未找到，请检查上传是否成功";
        }
        if (msg.contains("ParseException") || msg.contains("解析")) {
            return "数据格式解析失败，请检查文件编码和格式是否正确";
        }
        if (msg.contains("SQL") || msg.contains("Database") || msg.contains("数据库")) {
            return "数据库操作异常，请稍后重试";
        }
        if (msg.contains("OutOfMemory")) {
            return "文件数据量过大，内存不足，请拆分数据后重试";
        }
        if (msg.contains("Permission") || msg.contains("权限")) {
            return "文件读写权限不足，请检查输出目录";
        }
        if (msg.contains("timeout") || msg.contains("超时")) {
            return "处理超时，请稍后重试或减少数据量";
        }
        if (msg.length() > 100) {
            return msg.substring(0, 100) + "...";
        }
        return msg;
    }

    private void updateTaskStatus(TaskContext taskContext, TaskStatus status) {
        taskContext.setStatus(status);
        taskRepository.save(taskContext);
        progressService.updateProgress(taskContext.getTaskId(), status);
    }
}
