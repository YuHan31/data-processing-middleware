package com.middleware.org.cleaner;

import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;

/**
 * 数据清洗器接口
 * 每个 Cleaner 对应一个 ruleCode
 */
public interface IDataCleaner {

    /**
     * 执行清洗
     * @param processedData 待清洗的数据
     * @param taskContext 任务上下文（可用于获取参数）
     */
    void clean(ProcessedData processedData, TaskContext taskContext);

    /**
     * 返回该 Cleaner 对应的 ruleCode
     * 必须与数据库 clean_rule.rule_code 一致
     */
    String getRuleCode();

    /**
     * 获取清洗器展示名称
     */
    String getName();
}