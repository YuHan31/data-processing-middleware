package com.middleware.org.exporter;

import com.middleware.org.model.ProcessedData;

/**
 * 数据导出器接口
 */
public interface IDataExporter {

    /**
     * 导出数据
     * @param data 处理后的数据
     * @param outputPath 输出路径
     */
    void export(ProcessedData data, String outputPath) throws Exception;

    /**
     * 获取支持的导出格式
     * @return 导出格式（如：csv, xlsx, json）
     */
    String getSupportedFormat();
}
