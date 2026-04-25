package com.middleware.org.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON文件解析器
 */
@Component
public class JsonParser implements IDataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProcessedData parse(String filePath) throws Exception {
        List<DataRecord> records = new ArrayList<>();

        List<Map<String, Object>> jsonData = objectMapper.readValue(
            new File(filePath),
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );

        for (Map<String, Object> item : jsonData) {
            DataRecord record = new DataRecord();
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                record.addField(entry.getKey(), entry.getValue());
            }
            records.add(record);
        }

        ProcessedData processedData = new ProcessedData();
        processedData.setRecords(records);
        processedData.setTotalCount(records.size());
        processedData.setRecordCount(records.size());
        return processedData;
    }

    @Override
    public String getSupportedFileType() {
        return "json";
    }
}
