package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 转小写清洗器
 * ruleCode: TO_LOWER
 */
@Component
public class ToLowerCleaner implements IDataCleaner {

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof String) {
                    entry.setValue(((String) entry.getValue()).toLowerCase());
                }
            }
        }
    }

    @Override
    public String getRuleCode() {
        return "TO_LOWER";
    }

    @Override
    public String getName() {
        return "转小写";
    }
}