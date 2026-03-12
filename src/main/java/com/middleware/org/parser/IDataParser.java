package com.middleware.org.parser;

import com.middleware.org.model.DataRecord;
import java.util.List;

/**
 * 数据解析器接口
 * 定义统一的数据解析规范
 */
public interface IDataParser {

    /**
     * 解析文件数据
     * @param filePath 文件路径
     * @return 解析后的数据记录列表
     */
    List<DataRecord> parse(String filePath) throws Exception;

    /**
     * 获取支持的文件类型
     * @return 文件类型（如：csv, xlsx, json）
     */
    String getSupportedFileType();
}
