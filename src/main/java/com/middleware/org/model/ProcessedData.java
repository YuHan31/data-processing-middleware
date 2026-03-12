package com.middleware.org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理后的数据结构
 */
public class ProcessedData {
    private List<DataRecord> records;
    private int totalCount;
    private int validCount;
    private int invalidCount;

    public ProcessedData() {
        this.records = new ArrayList<>();
    }

    public List<DataRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DataRecord> records) {
        this.records = records;
    }

    public void addRecord(DataRecord record) {
        this.records.add(record);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }
}
