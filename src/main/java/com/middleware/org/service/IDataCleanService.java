package com.middleware.org.service;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ValidationResult;

import java.util.List;

/**
 * 数据清洗与校验接口
 * 对数据中的缺失值、格式错误和异常数据进行处理，提升数据质量
 */
public interface IDataCleanService {

    /**
     * 清洗数据
     * @param records 原始数据记录
     * @return 清洗后的数据记录
     */
    List<DataRecord> cleanData(List<DataRecord> records);

    /**
     * 校验数据
     * @param records 数据记录
     * @return 校验结果
     */
    ValidationResult validateData(List<DataRecord> records);

    /**
     * 处理缺失值
     * @param records 数据记录
     * @param strategy 处理策略（删除/填充/保留）
     * @return 处理后的数据记录
     */
    List<DataRecord> handleMissingValues(List<DataRecord> records, String strategy);

    /**
     * 去重
     * @param records 数据记录
     * @return 去重后的数据记录
     */
    List<DataRecord> removeDuplicates(List<DataRecord> records);

    /**
     * 数据标准化
     * @param records 数据记录
     * @return 标准化后的数据记录
     */
    List<DataRecord> normalizeData(List<DataRecord> records);
}
