package com.middleware.org.service.impl;

import com.middleware.org.exporter.ExporterFactory;
import com.middleware.org.exporter.IDataExporter;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.service.IDataOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据输出服务实现
 */
@Service
public class DataOutputServiceImpl implements IDataOutputService {

    @Autowired
    private ExporterFactory exporterFactory;

    @Override
    public void export(TaskContext taskContext) {
        try {
            ProcessedData processedData = taskContext.getProcessedData();
            if (processedData == null) {
                throw new RuntimeException("没有可导出的数据");
            }

            String outputPath = taskContext.getOutputFilePath();
            String outputFormat = detectOutputFormat(outputPath);

            IDataExporter exporter = exporterFactory.getExporter(outputFormat);
            exporter.export(processedData, outputPath);

        } catch (Exception e) {
            throw new RuntimeException("数据导出失败: " + e.getMessage(), e);
        }
    }

    private String detectOutputFormat(String outputPath) {
        if (outputPath.endsWith(".csv")) {
            return "CSV";
        } else if (outputPath.endsWith(".xlsx") || outputPath.endsWith(".xls")) {
            return "EXCEL";
        } else if (outputPath.endsWith(".json")) {
            return "JSON";
        }
        return "CSV";
    }
}
