package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据标准化处理器
 */
@Component
public class DataNormalizer {

    public void normalize(ProcessedData processedData) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof String) {
                    String normalized = normalizeString((String) entry.getValue());
                    entry.setValue(normalized);
                }
            }
        }
    }

    private String normalizeString(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("\\s+", " ");
    }
}
