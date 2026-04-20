package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 去除首尾空格清洗器
 * ruleCode: TRIM
 */
@Component
public class TrimCleaner implements IDataCleaner {

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof String) {
                    entry.setValue(((String) entry.getValue()).trim());
                }
            }
        }
    }

    @Override
    public String getRuleCode() {
        return "TRIM";
    }

    @Override
    public String getName() {
        return "去除首尾空格";
    }
}