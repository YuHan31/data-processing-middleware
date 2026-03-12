package com.middleware.org.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据记录
 * 系统内部统一的数据结构
 */
public class DataRecord {
    private Long recordId;
    private Map<String, Object> fields;
    private String sourceType;
    private Long timestamp;

    public DataRecord() {
        this.fields = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public DataRecord(Map<String, Object> fields) {
        this.fields = fields;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void addField(String key, Object value) {
        this.fields.put(key, value);
    }

    public Object getField(String key) {
        return this.fields.get(key);
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
