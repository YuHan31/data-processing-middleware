package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 去重清洗器
 * ruleCode: DEDUPLICATE
 */
@Component
public class DeduplicateCleaner implements IDataCleaner {

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        List<DataRecord> records = processedData.getRecords();
        Set<String> seen = new LinkedHashSet<>();
        List<DataRecord> uniqueRecords = new ArrayList<>();
        int removedCount = 0;

        for (DataRecord record : records) {
            String key = buildKey(record);
            if (!seen.contains(key)) {
                seen.add(key);
                uniqueRecords.add(record);
            } else {
                removedCount++;
            }
        }

        processedData.setRecords(uniqueRecords);
        processedData.setTotalCount(processedData.getTotalCount() - removedCount);
    }

    private String buildKey(DataRecord record) {
        Map<String, Object> fields = record.getFields();
        List<String> values = new ArrayList<>();
        for (Object v : fields.values()) {
            values.add(v != null ? v.toString() : "");
        }
        return String.join("|", values);
    }

    @Override
    public String getRuleCode() {
        return "DEDUPLICATE";
    }

    @Override
    public String getName() {
        return "去除重复";
    }
}