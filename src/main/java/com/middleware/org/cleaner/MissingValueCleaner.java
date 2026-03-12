package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 缺失值处理器
 */
@Component
public class MissingValueCleaner {

    public void clean(ProcessedData processedData) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                    entry.setValue("");
                    record.setValid(false);
                }
            }
        }
    }
}
