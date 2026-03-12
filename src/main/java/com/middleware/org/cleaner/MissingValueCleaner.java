package com.middleware.org.cleaner;

import com.middleware.org.model.CleanRule;
import com.middleware.org.model.DataRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 缺失值处理器
 */
@Component
public class MissingValueCleaner implements IDataCleaner {

    @Override
    public List<DataRecord> clean(List<DataRecord> records, CleanRule rule) {
        if (!rule.isHandleMissingValue()) {
            return records;
        }

        String strategy = rule.getMissingValueStrategy();

        for (DataRecord record : records) {
            Map<String, Object> fields = record.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                    handleMissingValue(entry, strategy);
                }
            }
        }

        return records;
    }

    private void handleMissingValue(Map.Entry<String, Object> entry, String strategy) {
        switch (strategy) {
            case "fill_default":
                entry.setValue("");
                break;
            case "fill_null":
                entry.setValue(null);
                break;
            case "fill_zero":
                entry.setValue("0");
                break;
            default:
                entry.setValue("");
        }
    }

    @Override
    public String getCleanerName() {
        return "MissingValueCleaner";
    }
}
