package com.middleware.org.service;

import com.middleware.org.model.DataRecord;

import java.util.List;

/**
 * 数据解析与格式转换接口
 * 实现对CSV、Excel、JSON等多种数据格式的解析，并将其统一转换为系统内部结构化数据
 */
public interface IDataParseService {

    /**
     * 解析文件
     * @param filePath 文件路径
     * @param fileType 文件类型（CSV/EXCEL/JSON等）
     * @return 解析后的数据记录列表
     */
    List<DataRecord> parseFile(String filePath, String fileType);

    /**
     * 转换数据格式
     * @param records 原始数据记录
     * @param targetFormat 目标格式
     * @return 转换后的数据记录
     */
    List<DataRecord> convertFormat(List<DataRecord> records, String targetFormat);

    /**
     * 获取支持的文件类型
     * @return 支持的文件类型列表
     */
    List<String> getSupportedFileTypes();
}
