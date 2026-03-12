package com.middleware.org.service.impl;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ValidationResult;
import com.middleware.org.service.IDataCleanService;

import java.util.*;

/**
 * 数据清洗与校验服务实现
 */
public class DataCleanServiceImpl implements IDataCleanService {

    @Override
    public List<DataRecord> cleanData(List<DataRecord> records) {
        // 综合清洗流程
        List<DataRecord> cleaned = handleMissingValues(records, "REMOVE");
        cleaned = removeDuplicates(cleaned);
        cleaned = normalizeData(cleaned);
        return cleaned;
    }

    @Override
    public ValidationResult validateData(List<DataRecord> records) {
        ValidationResult result = new ValidationResult();
        result.setTotalRecords(records.size());

        int validCount = 0;
        int invalidCount = 0;

        for (DataRecord record : records) {
            boolean isValid = true;

            // 检查空值
            for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
                if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                    result.addWarning("记录 " + record.getRecordId() + " 字段 " + entry.getKey() + " 为空");
                    isValid = false;
                }
            }

            if (isValid) {
                validCount++;
            } else {
                invalidCount++;
            }
        }

        result.setValidRecords(validCount);
        result.setInvalidRecords(invalidCount);
        result.setValid(invalidCount == 0);

        return result;
    }

    @Override
    public List<DataRecord> handleMissingValues(List<DataRecord> records, String strategy) {
        List<DataRecord> result = new ArrayList<>();

        for (DataRecord record : records) {
            boolean hasNull = false;

            for (Object value : record.getFields().values()) {
                if (value == null || value.toString().trim().isEmpty()) {
                    hasNull = true;
                    break;
                }
            }

            switch (strategy.toUpperCase()) {
                case "REMOVE":
                    if (!hasNull) {
                        result.add(record);
                    }
                    break;
                case "FILL":
                    // 用默认值填充
                    for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
                        if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                            entry.setValue("N/A");
                        }
                    }
                    result.add(record);
                    break;
                case "KEEP":
                default:
                    result.add(record);
                    break;
            }
        }

        return result;
    }

    @Override
    public List<DataRecord> removeDuplicates(List<DataRecord> records) {
        Set<String> seen = new HashSet<>();
        List<DataRecord> result = new ArrayList<>();

        for (DataRecord record : records) {
            String key = record.getFields().toString();
            if (!seen.contains(key)) {
                seen.add(key);
                result.add(record);
            }
        }

        return result;
    }

    @Override
    public List<DataRecord> normalizeData(List<DataRecord> records) {
        // 数据标准化：去除首尾空格、统一大小写等
        for (DataRecord record : records) {
            Map<String, Object> normalizedFields = new HashMap<>();

            for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
                String key = entry.getKey().trim();
                Object value = entry.getValue();

                if (value instanceof String) {
                    value = ((String) value).trim();
                }

                normalizedFields.put(key, value);
            }

            record.setFields(normalizedFields);
        }

        return records;
    }
}
