package com.middleware.org.cleaner;

import com.middleware.org.model.CleanRule;
import com.middleware.org.model.DataRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 清洗链
 * 按顺序执行多个清洗器
 */
@Component
public class CleanerChain {

    @Autowired
    private MissingValueCleaner missingValueCleaner;

    @Autowired
    private FormatValidator formatValidator;

    @Autowired
    private DataNormalizer dataNormalizer;

    public List<DataRecord> execute(List<DataRecord> records, CleanRule rule) {
        records = dataNormalizer.clean(records, rule);
        records = missingValueCleaner.clean(records, rule);
        records = formatValidator.clean(records, rule);
        return records;
    }
}
