package com.middleware.org.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * JSON导出器
 */
@Component
public class JsonExporter implements IDataExporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void export(ProcessedData data, String outputPath) throws Exception {
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(outputPath), data.getRecords());
    }

    @Override
    public String getSupportedFormat() {
        return "json";
    }
}
