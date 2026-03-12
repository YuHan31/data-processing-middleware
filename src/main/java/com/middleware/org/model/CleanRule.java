package com.middleware.org.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 清洗规则配置
 */
public class CleanRule {
    private boolean handleMissingValue;
    private String missingValueStrategy;
    private Map<String, String> fieldTypeMap;
    private Map<String, String> fieldFormatMap;
    private boolean removeInvalidRecords;
    private boolean normalizeData;

    public CleanRule() {
        this.fieldTypeMap = new HashMap<>();
        this.fieldFormatMap = new HashMap<>();
        this.handleMissingValue = true;
        this.missingValueStrategy = "fill_default";
        this.removeInvalidRecords = false;
        this.normalizeData = true;
    }

    public boolean isHandleMissingValue() {
        return handleMissingValue;
    }

    public void setHandleMissingValue(boolean handleMissingValue) {
        this.handleMissingValue = handleMissingValue;
    }

    public String getMissingValueStrategy() {
        return missingValueStrategy;
    }

    public void setMissingValueStrategy(String missingValueStrategy) {
        this.missingValueStrategy = missingValueStrategy;
    }

    public Map<String, String> getFieldTypeMap() {
        return fieldTypeMap;
    }

    public void setFieldTypeMap(Map<String, String> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }

    public void addFieldType(String fieldName, String fieldType) {
        this.fieldTypeMap.put(fieldName, fieldType);
    }

    public Map<String, String> getFieldFormatMap() {
        return fieldFormatMap;
    }

    public void setFieldFormatMap(Map<String, String> fieldFormatMap) {
        this.fieldFormatMap = fieldFormatMap;
    }

    public void addFieldFormat(String fieldName, String format) {
        this.fieldFormatMap.put(fieldName, format);
    }

    public boolean isRemoveInvalidRecords() {
        return removeInvalidRecords;
    }

    public void setRemoveInvalidRecords(boolean removeInvalidRecords) {
        this.removeInvalidRecords = removeInvalidRecords;
    }

    public boolean isNormalizeData() {
        return normalizeData;
    }

    public void setNormalizeData(boolean normalizeData) {
        this.normalizeData = normalizeData;
    }
}
