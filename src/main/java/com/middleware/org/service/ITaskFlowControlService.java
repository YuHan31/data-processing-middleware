package com.middleware.org.service;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.model.TaskStatus;

import java.util.List;
import java.util.Map;

/**
 * 任务流程控制服务接口
 */
public interface ITaskFlowControlService {

    /**
     * 创建任务
     * @param taskContext 任务上下文
     * @return 任务ID
     */
    String createTask(TaskContext taskContext);

    /**
     * 启动任务
     * @param taskId 任务ID
     */
    void startTask(String taskId);

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    TaskStatus getTaskStatus(String taskId);

    /**
     * 分页获取任务列表
     * @param page 页码（从1开始）
     * @param size 每页大小
     */
    Map<String, Object> listTasksByPage(int page, int size);

    /**
     * 停止任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean stopTask(String taskId);

    /**
     * 获取任务上下文
     * @param taskId 任务ID
     * @return 任务上下文
     */
    TaskContext getTaskContext(String taskId);
}
