package com.middleware.org.cleaner;

import com.middleware.org.model.CleanRule;
import com.middleware.org.model.DataRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据标准化处理器
 */
@Component
public class DataNormalizer implements IDataCleaner {

    @Override
    public List<DataRecord> clean(List<DataRecord> records, CleanRule rule) {
        if (!rule.isNormalizeData()) {
            return records;
        }

        for (DataRecord record : records) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof String) {
                    String normalized = normalizeString((String) entry.getValue());
                    entry.setValue(normalized);
                }
            }
        }

        return records;
    }

    private String normalizeString(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("\\s+", " ");
    }

    @Override
    public String getCleanerName() {
        return "DataNormalizer";
    }
}
