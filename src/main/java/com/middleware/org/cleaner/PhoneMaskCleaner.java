package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号脱敏清洗器
 * ruleCode: PHONE_MASK
 * 手机号 → 138****1234
 */
@Component
public class PhoneMaskCleaner implements IDataCleaner {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(1[3-9]\\d{9})");

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String fieldName = entry.getKey().toLowerCase();
                if ((fieldName.contains("phone") || fieldName.contains("mobile") || fieldName.contains("电话"))
                        && entry.getValue() instanceof String) {
                    entry.setValue(maskPhone((String) entry.getValue()));
                }
            }
        }
    }

    private String maskPhone(String value) {
        if (value == null) return "";
        Matcher m = PHONE_PATTERN.matcher(value);
        if (m.find()) {
            String phone = m.group(1);
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return value;
    }

    @Override
    public String getRuleCode() {
        return "PHONE_MASK";
    }

    @Override
    public String getName() {
        return "手机号脱敏";
    }
}