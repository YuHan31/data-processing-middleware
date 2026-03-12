package com.middleware.org.service.impl;

import com.middleware.org.exception.DataParseException;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.parser.IDataParser;
import com.middleware.org.parser.ParserFactory;
import com.middleware.org.service.IDataParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据解析服务实现
 */
@Service
public class DataParseServiceImpl implements IDataParseService {

    @Autowired
    private ParserFactory parserFactory;

    @Override
    public void parse(TaskContext taskContext) {
        try {
            String filePath = taskContext.getInputFilePath();
            String fileType = taskContext.getFileType();

            IDataParser parser = parserFactory.getParser(fileType);
            ProcessedData processedData = parser.parse(filePath);

            taskContext.setProcessedData(processedData);

        } catch (Exception e) {
            throw new DataParseException("数据解析失败: " + e.getMessage(), e);
        }
    }
}
