package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.CleanRule;

import java.util.List;

/**
 * 数据清洗器接口
 */
public interface IDataCleaner {

    /**
     * 清洗数据
     * @param records 原始数据记录
     * @param rule 清洗规则
     * @return 清洗后的数据记录
     */
    List<DataRecord> clean(List<DataRecord> records, CleanRule rule);

    /**
     * 获取清洗器名称
     * @return 清洗器名称
     */
    String getCleanerName();
}
