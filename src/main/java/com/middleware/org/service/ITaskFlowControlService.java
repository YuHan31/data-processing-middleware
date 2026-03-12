package com.middleware.org.service;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;

/**
 * 任务启动与流程控制接口
 * 负责统一管理数据处理任务的执行流程
 */
public interface ITaskFlowControlService {

    /**
     * 启动任务
     * @param taskContext 任务上下文
     * @return 任务ID
     */
    String startTask(TaskContext taskContext);

    /**
     * 停止任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean stopTask(String taskId);

    /**
     * 查询任务状态
     * @param taskId 任务ID
     * @return 任务结果
     */
    TaskResult getTaskStatus(String taskId);

    /**
     * 获取所有任务列表
     * @return 任务列表
     */
    java.util.List<TaskResult> listAllTasks();
}
