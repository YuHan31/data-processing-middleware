package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用数据脱敏清洗器
 * ruleCode: DATA_MASK
 */
@Component
public class DataMaskCleaner implements IDataCleaner {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(1[3-9]\\d{9})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof String) {
                    String value = (String) entry.getValue();
                    String fieldName = entry.getKey().toLowerCase();
                    String masked = maskValue(value, fieldName);
                    entry.setValue(masked);
                }
            }
        }
    }

    private String maskValue(String value, String fieldName) {
        if (fieldName.contains("phone") || fieldName.contains("mobile") || fieldName.contains("电话")) {
            return maskPhone(value);
        }
        if (fieldName.contains("email") || fieldName.contains("邮箱")) {
            return maskEmail(value);
        }
        if (fieldName.contains("idcard") || fieldName.contains("id_card") || fieldName.contains("身份证")) {
            return maskIdCard(value);
        }
        if (fieldName.contains("name") || fieldName.contains("姓名")) {
            return maskName(value);
        }
        return maskGeneric(value);
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

    private String maskIdCard(String value) {
        if (value == null || value.length() < 8) return value;
        return value.substring(0, 3) + "***********" + value.substring(value.length() - 4);
    }

    private String maskName(String value) {
        if (value == null || value.length() <= 1) return value;
        return value.charAt(0) + "***";
    }

    private String maskGeneric(String value) {
        if (value == null || value.length() <= 4) return value;
        int len = value.length();
        return value.substring(0, 2) + "****" + value.substring(len - 2);
    }

    @Override
    public String getRuleCode() {
        return "DATA_MASK";
    }

    @Override
    public String getName() {
        return "数据脱敏";
    }
}