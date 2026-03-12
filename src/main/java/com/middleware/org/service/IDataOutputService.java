package com.middleware.org.service;

import com.middleware.org.model.DataRecord;

import java.util.List;

/**
 * 结果输出适配接口
 * 提供标准化、结构化的数据输出能力，支持多格式导出及统一对外接口服务
 */
public interface IDataOutputService {

    /**
     * 输出到CSV文件
     * @param records 数据记录
     * @param outputPath 输出路径
     * @return 是否成功
     */
    boolean outputToCsv(List<DataRecord> records, String outputPath);

    /**
     * 输出到Excel文件
     * @param records 数据记录
     * @param outputPath 输出路径
     * @return 是否成功
     */
    boolean outputToExcel(List<DataRecord> records, String outputPath);

    /**
     * 输出到JSON文件
     * @param records 数据记录
     * @param outputPath 输出路径
     * @return 是否成功
     */
    boolean outputToJson(List<DataRecord> records, String outputPath);

    /**
     * 输出到数据库
     * @param records 数据记录
     * @param connectionId 数据库连接ID
     * @param tableName 表名
     * @return 是否成功
     */
    boolean outputToDatabase(List<DataRecord> records, String connectionId, String tableName);

    /**
     * 获取支持的输出格式
     * @return 支持的输出格式列表
     */
    List<String> getSupportedOutputFormats();
}
