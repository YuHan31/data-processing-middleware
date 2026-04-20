package com.middleware.org.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对比 DTO
 */
public class DataCompareDTO {

    private int index;
    private Map<String, Object> before = new HashMap<>();
    private Map<String, Object> after = new HashMap<>();
    private List<FieldChange> changedFields = new ArrayList<>();

    public DataCompareDTO() {}

    public DataCompareDTO(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<String, Object> getBefore() {
        return before;
    }

    public void setBefore(Map<String, Object> before) {
        this.before = before;
    }

    public Map<String, Object> getAfter() {
        return after;
    }

    public void setAfter(Map<String, Object> after) {
        this.after = after;
    }

    public List<FieldChange> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(List<FieldChange> changedFields) {
        this.changedFields = changedFields;
    }

    public void addChange(String field, Object beforeVal, Object afterVal) {
        changedFields.add(new FieldChange(field, beforeVal, afterVal));
    }

    /**
     * 字段变化详情
     */
    public static class FieldChange {
        private String field;
        private Object beforeValue;
        private Object afterValue;

        public FieldChange() {}

        public FieldChange(String field, Object beforeValue, Object afterValue) {
            this.field = field;
            this.beforeValue = beforeValue;
            this.afterValue = afterValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getBeforeValue() {
            return beforeValue;
        }

        public void setBeforeValue(Object beforeValue) {
            this.beforeValue = beforeValue;
        }

        public Object getAfterValue() {
            return afterValue;
        }

        public void setAfterValue(Object afterValue) {
            this.afterValue = afterValue;
        }
    }
}