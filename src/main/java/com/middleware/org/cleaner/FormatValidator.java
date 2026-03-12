package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 格式校验器
 */
@Component
public class FormatValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public void clean(ProcessedData processedData) {
        for (DataRecord record : processedData.getRecords()) {
            validateRecord(record);
        }
    }

    private void validateRecord(DataRecord record) {
        Map<String, Object> fields = record.getFields();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value.toString().trim().isEmpty()) {
                record.setValid(false);
            }
        }
    }
}
