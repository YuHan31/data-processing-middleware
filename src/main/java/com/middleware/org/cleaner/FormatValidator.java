package com.middleware.org.cleaner;

import com.middleware.org.model.CleanRule;
import com.middleware.org.model.DataRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 格式校验器
 */
@Component
public class FormatValidator implements IDataCleaner {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    @Override
    public List<DataRecord> clean(List<DataRecord> records, CleanRule rule) {
        List<DataRecord> validRecords = new ArrayList<>();
        Map<String, String> fieldTypeMap = rule.getFieldTypeMap();

        for (DataRecord record : records) {
            boolean isValid = validateRecord(record, fieldTypeMap);

            if (isValid || !rule.isRemoveInvalidRecords()) {
                if (!isValid) {
                    record.addField("_validation_error", "格式校验失败");
                }
                validRecords.add(record);
            }
        }

        return validRecords;
    }

    private boolean validateRecord(DataRecord record, Map<String, String> fieldTypeMap) {
        for (Map.Entry<String, String> typeEntry : fieldTypeMap.entrySet()) {
            String fieldName = typeEntry.getKey();
            String expectedType = typeEntry.getValue();
            Object value = record.getField(fieldName);

            if (value == null) continue;

            String valueStr = value.toString();
            if (!validateFieldType(valueStr, expectedType)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateFieldType(String value, String type) {
        switch (type.toLowerCase()) {
            case "number":
            case "integer":
            case "double":
                return NUMBER_PATTERN.matcher(value).matches();
            case "email":
                return EMAIL_PATTERN.matcher(value).matches();
            case "string":
                return true;
            default:
                return true;
        }
    }

    @Override
    public String getCleanerName() {
        return "FormatValidator";
    }
}
