package com.middleware.org.cleaner;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期标准化清洗器
 * ruleCode: NORMALIZE_DATE
 * 将各种日期格式统一为 yyyy-MM-dd
 */
@Component
public class NormalizeDateCleaner implements IDataCleaner {

    private static final Pattern[] DATE_PATTERNS = {
            Pattern.compile("(\\d{4})[年/](\\d{1,2})[月/](\\d{1,2})日?"),
            Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})"),
            Pattern.compile("(\\d{4})(\\d{2})(\\d{2})"),
    };

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        for (DataRecord record : processedData.getRecords()) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String fieldName = entry.getKey().toLowerCase();
                if ((fieldName.contains("date") || fieldName.contains("日期") || fieldName.contains("time") || fieldName.contains("时间"))
                        && entry.getValue() instanceof String) {
                    String normalized = normalizeDate((String) entry.getValue());
                    if (normalized != null) {
                        entry.setValue(normalized);
                    }
                }
            }
        }
    }

    private String normalizeDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        value = value.trim();

        // 尝试直接解析标准格式
        try {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return value;
        } catch (DateTimeParseException ignored) {}

        // 尝试解析 yyyy/MM/dd 等
        for (Pattern p : DATE_PATTERNS) {
            Matcher m = p.matcher(value);
            if (m.find()) {
                String year = m.group(1);
                String month = String.format("%02d", Integer.parseInt(m.group(2)));
                String day = String.format("%02d", Integer.parseInt(m.group(3)));
                return year + "-" + month + "-" + day;
            }
        }

        // 尝试解析各种非标准格式
        try {
            LocalDateTime.parse(value.replace("/", "-").replace(".", "-"),
                    DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"));
            return value;
        } catch (DateTimeParseException ignored) {}

        return null; // 无法标准化，保持原值
    }

    @Override
    public String getRuleCode() {
        return "NORMALIZE_DATE";
    }

    @Override
    public String getName() {
        return "日期标准化";
    }
}