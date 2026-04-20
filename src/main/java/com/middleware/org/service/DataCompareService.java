package com.middleware.org.service;

import com.middleware.org.dto.DataCompareDTO;
import com.middleware.org.model.DataRecord;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据对比服务
 */
@Service
public class DataCompareService {

    /**
     * 对比原始数据和处理后数据（支持搜索）
     *
     * @param originalRecords 原始数据
     * @param cleanedRecords  清洗后数据
     * @param onlyChanged     是否只返回有变化的记录
     * @param search          搜索关键词（匹配字段名或字段值）
     * @param page            页码（从1开始）
     * @param size            每页条数
     * @return 对比结果（分页）
     */
    public Map<String, Object> compare(
            List<DataRecord> originalRecords,
            List<DataRecord> cleanedRecords,
            boolean onlyChanged,
            String search,
            int page,
            int size) {

        // 构建完整对比
        List<DataCompareDTO> allCompares = buildCompares(originalRecords, cleanedRecords);

        // 1. 过滤有变化的
        if (onlyChanged) {
            allCompares = allCompares.stream()
                    .filter(c -> !c.getChangedFields().isEmpty())
                    .collect(Collectors.toList());
        }

        // 2. 搜索过滤
        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            allCompares = allCompares.stream()
                    .filter(dto -> matchesSearch(dto, keyword))
                    .collect(Collectors.toList());
        }

        int total = allCompares.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<DataCompareDTO> pageData = fromIndex < total ? allCompares.subList(fromIndex, toIndex) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", total == 0 ? 0 : (int) Math.ceil((double) total / size));
        result.put("onlyChanged", onlyChanged);
        result.put("search", search != null ? search : "");
        result.put("list", pageData);
        return result;
    }

    /**
     * 构建 diff 统计信息（类似 gitlab 变更统计）
     */
    public Map<String, Object> buildDiffStats(
            List<DataRecord> originalRecords,
            List<DataRecord> cleanedRecords) {

        List<DataCompareDTO> allCompares = buildCompares(originalRecords, cleanedRecords);

        int totalRecords = allCompares.size();
        int changedRecords = 0;
        int addedRecords = 0;
        int deletedRecords = 0;

        Map<String, DiffStat> fieldStats = new LinkedHashMap<>();

        for (DataCompareDTO dto : allCompares) {
            boolean isChanged = !dto.getChangedFields().isEmpty();
            if (isChanged) {
                changedRecords++;
            }

            // 统计每个字段的变化
            for (DataCompareDTO.FieldChange change : dto.getChangedFields()) {
                String field = change.getField();
                DiffStat stat = fieldStats.computeIfAbsent(field, k -> new DiffStat(field));

                if (change.getBeforeValue() == null || change.getBeforeValue().toString().trim().isEmpty()) {
                    stat.additions++;
                } else if (change.getAfterValue() == null || change.getAfterValue().toString().trim().isEmpty()) {
                    stat.deletions++;
                } else {
                    stat.modifications++;
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", totalRecords);
        stats.put("changedRecords", changedRecords);
        stats.put("unchangedRecords", totalRecords - changedRecords);
        stats.put("changedRate", totalRecords == 0 ? 0 : Math.round((double) changedRecords / totalRecords * 10000) / 100.0);
        stats.put("fieldStats", new ArrayList<>(fieldStats.values()));
        return stats;
    }

    /**
     * 按字段名获取该字段的所有变化
     */
    public Map<String, Object> getFieldDiff(
            List<DataRecord> originalRecords,
            List<DataRecord> cleanedRecords,
            String fieldName,
            int page,
            int size) {

        List<DataCompareDTO> allCompares = buildCompares(originalRecords, cleanedRecords);

        // 筛选包含该字段变化的记录
        List<FieldDiffEntry> entries = new ArrayList<>();
        for (DataCompareDTO dto : allCompares) {
            for (DataCompareDTO.FieldChange change : dto.getChangedFields()) {
                if (change.getField().equalsIgnoreCase(fieldName)) {
                    FieldDiffEntry entry = new FieldDiffEntry();
                    entry.setIndex(dto.getIndex());
                    entry.setField(change.getField());
                    entry.setBeforeValue(change.getBeforeValue());
                    entry.setAfterValue(change.getAfterValue());
                    entries.add(entry);
                    break; // 每条记录只取该字段第一个变化
                }
            }
        }

        int total = entries.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<FieldDiffEntry> pageData = fromIndex < total ? entries.subList(fromIndex, toIndex) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("field", fieldName);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", total == 0 ? 0 : (int) Math.ceil((double) total / size));
        result.put("list", pageData);
        return result;
    }

    /**
     * 搜索匹配（匹配字段名、原始值、目标值）
     */
    private boolean matchesSearch(DataCompareDTO dto, String keyword) {
        // 检查 index
        if (String.valueOf(dto.getIndex()).contains(keyword)) {
            return true;
        }

        // 检查字段名
        for (DataCompareDTO.FieldChange change : dto.getChangedFields()) {
            if (change.getField().toLowerCase().contains(keyword)) {
                return true;
            }
            String beforeStr = change.getBeforeValue() != null ? change.getBeforeValue().toString() : "";
            String afterStr = change.getAfterValue() != null ? change.getAfterValue().toString() : "";
            if (beforeStr.toLowerCase().contains(keyword) || afterStr.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        // 检查 before/after 完整数据
        for (Object val : dto.getBefore().values()) {
            if (val != null && val.toString().toLowerCase().contains(keyword)) {
                return true;
            }
        }
        for (Object val : dto.getAfter().values()) {
            if (val != null && val.toString().toLowerCase().contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 构建完整对比列表
     */
    private List<DataCompareDTO> buildCompares(
            List<DataRecord> originalRecords,
            List<DataRecord> cleanedRecords) {

        List<DataCompareDTO> results = new ArrayList<>();

        if (originalRecords == null || originalRecords.isEmpty()) {
            return results;
        }

        int maxLen = Math.max(originalRecords.size(), cleanedRecords != null ? cleanedRecords.size() : 0);

        for (int i = 0; i < maxLen; i++) {
            DataCompareDTO dto = new DataCompareDTO(i);

            DataRecord original = i < originalRecords.size() ? originalRecords.get(i) : null;
            DataRecord cleaned = cleanedRecords != null && i < cleanedRecords.size() ? cleanedRecords.get(i) : null;

            Map<String, Object> beforeMap = original != null ? original.getFields() : new HashMap<>();
            Map<String, Object> afterMap = cleaned != null ? cleaned.getFields() : new HashMap<>();

            dto.setBefore(new HashMap<>(beforeMap));
            dto.setAfter(new HashMap<>(afterMap));

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(beforeMap.keySet());
            allKeys.addAll(afterMap.keySet());

            for (String key : allKeys) {
                Object beforeVal = beforeMap.get(key);
                Object afterVal = afterMap.get(key);
                if (!Objects.equals(beforeVal, afterVal)) {
                    dto.addChange(key, beforeVal, afterVal);
                }
            }

            results.add(dto);
        }

        return results;
    }

    /**
     * 获取统计摘要
     */
    public Map<String, Object> getSummary(
            List<DataRecord> originalRecords,
            List<DataRecord> cleanedRecords) {
        return buildDiffStats(originalRecords, cleanedRecords);
    }

    // --- 内部类 ---

    /**
     * 字段差异统计
     */
    public static class DiffStat {
        public String field;
        public int additions;    // 新增/从空变有值
        public int deletions;     // 删除/从有值变空
        public int modifications; // 修改

        public DiffStat() {}

        public DiffStat(String field) {
            this.field = field;
        }

        public int getTotal() {
            return additions + deletions + modifications;
        }
    }

    /**
     * 字段变化条目（用于按字段查看）
     */
    public static class FieldDiffEntry {
        private int index;
        private String field;
        private Object beforeValue;
        private Object afterValue;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
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