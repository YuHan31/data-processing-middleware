package com.middleware.org.service;

import com.middleware.org.model.TaskContext;

/**
 * 数据解析服务接口
 */
public interface IDataParseService {

    /**
     * 解析数据
     * @param taskContext 任务上下文
     */
    void parse(TaskContext taskContext);
}
