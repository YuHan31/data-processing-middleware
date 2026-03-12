package com.middleware.org.service.impl;

import com.middleware.org.cleaner.CleanerChain;
import com.middleware.org.exception.DataCleanException;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.service.IDataCleanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据清洗服务实现
 */
@Service
public class DataCleanServiceImpl implements IDataCleanService {

    @Autowired
    private CleanerChain cleanerChain;

    @Override
    public void clean(TaskContext taskContext) {
        try {
            ProcessedData processedData = taskContext.getProcessedData();
            if (processedData == null) {
                throw new DataCleanException("没有可清洗的数据");
            }

            cleanerChain.clean(processedData);

        } catch (Exception e) {
            throw new DataCleanException("数据清洗失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void normalize(TaskContext taskContext) {
        try {
            ProcessedData processedData = taskContext.getProcessedData();
            if (processedData == null) {
                throw new DataCleanException("没有可标准化的数据");
            }

            cleanerChain.normalize(processedData);

        } catch (Exception e) {
            throw new DataCleanException("数据标准化失败: " + e.getMessage(), e);
        }
    }
}
