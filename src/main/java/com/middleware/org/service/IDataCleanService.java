package com.middleware.org.service;

import com.middleware.org.model.TaskContext;

/**
 * 数据清洗服务接口
 */
public interface IDataCleanService {

    /**
     * 清洗数据
     * @param taskContext 任务上下文
     */
    void clean(TaskContext taskContext);

    /**
     * 标准化数据
     * @param taskContext 任务上下文
     */
    void normalize(TaskContext taskContext);
}
