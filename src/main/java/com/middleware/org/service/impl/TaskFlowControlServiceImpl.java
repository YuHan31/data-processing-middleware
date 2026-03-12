package com.middleware.org.service.impl;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 任务启动与流程控制服务实现
 */
public class TaskFlowControlServiceImpl implements ITaskFlowControlService {

    private final Map<String, TaskResult> tasks = new ConcurrentHashMap<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private final Random random = new Random();

    private final IFilePreprocessService filePreprocessService;
    private final IDataParseService dataParseService;
    private final IDataCleanService dataCleanService;
    private final IDataOutputService dataOutputService;
    private final ILogService logService;

    public TaskFlowControlServiceImpl(
            IFilePreprocessService filePreprocessService,
            IDataParseService dataParseService,
            IDataCleanService dataCleanService,
            IDataOutputService dataOutputService,
            ILogService logService) {
        this.filePreprocessService = filePreprocessService;
        this.dataParseService = dataParseService;
        this.dataCleanService = dataCleanService;
        this.dataOutputService = dataOutputService;
        this.logService = logService;
    }

    @Override
    public String startTask(TaskContext taskContext) {
        String taskId = generateTaskId();
        taskContext.setTaskId(taskId);

        TaskResult taskResult = new TaskResult(taskId, taskContext.getTaskName(), "Pending");
        taskResult.setInputFilePath(taskContext.getInputFilePath());
        taskResult.setOutputFilePath(taskContext.getOutputFilePath());
        taskResult.setStartTime(System.currentTimeMillis());
        tasks.put(taskId, taskResult);

        logService.info("任务启动: " + taskId + " - " + taskContext.getTaskName());

        threadPool.submit(() -> executeTask(taskContext, taskResult));

        return taskId;
    }

    private void executeTask(TaskContext taskContext, TaskResult taskResult) {
        try {
            taskResult.setStatus("Running");
            logService.info("开始执行任务: " + taskContext.getTaskId());

            // 1. 文件预处理
            String preprocessedFile = filePreprocessService.preprocessFile(taskContext.getInputFilePath());
            logService.info("文件预处理完成: " + preprocessedFile);

            // 2. 数据解析
            List<DataRecord> records = dataParseService.parseFile(
                    preprocessedFile, taskContext.getFileType());
            logService.info("数据解析完成，共 " + records.size() + " 条记录");

            // 3. 数据清洗
            records = dataCleanService.cleanData(records);
            logService.info("数据清洗完成");

            // 4. 数据输出
            boolean success = dataOutputService.outputToCsv(records, taskContext.getOutputFilePath());

            if (success) {
                taskResult.setStatus("Completed");
                taskResult.setProcessedRecords(records.size());
                logService.info("任务完成: " + taskContext.getTaskId());
            } else {
                taskResult.setStatus("Failed");
                taskResult.setMessage("数据输出失败");
                logService.error("任务失败: " + taskContext.getTaskId());
            }

        } catch (Exception e) {
            taskResult.setStatus("Failed");
            taskResult.setMessage(e.getMessage());
            logService.error("任务异常: " + taskContext.getTaskId(), e);
        } finally {
            taskResult.setEndTime(System.currentTimeMillis());
        }
    }

    @Override
    public boolean stopTask(String taskId) {
        TaskResult taskResult = tasks.get(taskId);
        if (taskResult == null) {
            logService.warn("尝试停止不存在的任务: " + taskId);
            return false;
        }

        taskResult.setStatus("Stopped");
        logService.info("任务已停止: " + taskId);
        return true;
    }

    @Override
    public TaskResult getTaskStatus(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public List<TaskResult> listAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    private String generateTaskId() {
        int id = random.nextInt(9000) + 1000;
        return String.valueOf(id);
    }
}
