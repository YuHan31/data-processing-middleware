package com.middleware.org.cleaner;

import com.middleware.org.model.ProcessedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 清洗链
 */
@Component
public class CleanerChain {

    @Autowired
    private MissingValueCleaner missingValueCleaner;

    @Autowired
    private FormatValidator formatValidator;

    @Autowired
    private DataNormalizer dataNormalizer;

    public void clean(ProcessedData processedData) {
        missingValueCleaner.clean(processedData);
        formatValidator.clean(processedData);
    }

    public void normalize(ProcessedData processedData) {
        dataNormalizer.normalize(processedData);
    }
}
