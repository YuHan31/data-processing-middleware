package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮箱脱敏清洗器
 * ruleCode: EMAIL_MASK
 * 邮箱 → a***@xx.com
 */
@Component
public class EmailMaskCleaner implements IDataCleaner {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String fieldName = entry.getKey().toLowerCase();
                if ((fieldName.contains("email") || fieldName.contains("邮箱"))
                        && entry.getValue() instanceof String) {
                    entry.setValue(maskEmail((String) entry.getValue()));
                }
            }
        }
    }

    private String maskEmail(String value) {
        if (value == null) return "";
        Matcher m = EMAIL_PATTERN.matcher(value);
        if (m.find()) {
            String local = m.group(1);
            String domain = m.group(2);
            char first = local.charAt(0);
            return first + "***@" + domain;
        }
        return value;
    }

    @Override
    public String getRuleCode() {
        return "EMAIL_MASK";
    }

    @Override
    public String getName() {
        return "邮箱脱敏";
    }
}