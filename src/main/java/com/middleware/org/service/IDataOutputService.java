package com.middleware.org.service;

import com.middleware.org.model.TaskContext;

/**
 * 数据输出服务接口
 */
public interface IDataOutputService {

    /**
     * 导出数据
     * @param taskContext 任务上下文
     */
    void export(TaskContext taskContext);
}
