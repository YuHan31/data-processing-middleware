package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 删除空值行清洗器
 * ruleCode: REMOVE_NULL
 */
@Component
public class RemoveNullCleaner implements IDataCleaner {

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        List<DataRecord> records = processedData.getRecords();
        List<DataRecord> validRecords = new ArrayList<>();
        int removedCount = 0;

        for (DataRecord record : records) {
            Map<String, Object> fields = record.getFields();
            boolean allEmpty = fields.values().stream()
                    .allMatch(v -> v == null || v.toString().trim().isEmpty());
            if (!allEmpty) {
                validRecords.add(record);
            } else {
                removedCount++;
            }
        }

        processedData.setRecords(validRecords);
        processedData.setTotalCount(processedData.getTotalCount() - removedCount);
        processedData.setRecordCount(validRecords.size());
    }

    @Override
    public String getRuleCode() {
        return "REMOVE_NULL";
    }

    @Override
    public String getName() {
        return "删除空值行";
    }
}