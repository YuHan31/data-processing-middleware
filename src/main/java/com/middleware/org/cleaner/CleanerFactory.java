package com.middleware.org.cleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cleaner 工厂
 * 根据 ruleCode 自动获取对应的 Cleaner 实例
 *
 * 使用方式：
 *   IDataCleaner cleaner = cleanerFactory.get("TRIM");
 */
@Component
public class CleanerFactory {

    @Autowired
    private TrimCleaner trimCleaner;

    @Autowired
    private ToLowerCleaner toLowerCleaner;

    @Autowired
    private ToUpperCleaner toUpperCleaner;

    @Autowired
    private RemoveNullCleaner removeNullCleaner;

    @Autowired
    private RemoveEmptyRowCleaner removeEmptyRowCleaner;

    @Autowired
    private DeduplicateCleaner deduplicateCleaner;

    @Autowired
    private DataMaskCleaner dataMaskCleaner;

    @Autowired
    private PhoneMaskCleaner phoneMaskCleaner;

    @Autowired
    private EmailMaskCleaner emailMaskCleaner;

    @Autowired
    private NormalizeDateCleaner normalizeDateCleaner;

    private final Map<String, IDataCleaner> cleanerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        register(trimCleaner);
        register(toLowerCleaner);
        register(toUpperCleaner);
        register(removeNullCleaner);
        register(removeEmptyRowCleaner);
        register(deduplicateCleaner);
        register(dataMaskCleaner);
        register(phoneMaskCleaner);
        register(emailMaskCleaner);
        register(normalizeDateCleaner);
    }

    private void register(IDataCleaner cleaner) {
        cleanerMap.put(cleaner.getRuleCode(), cleaner);
    }

    /**
     * 根据 ruleCode 获取 Cleaner
     *
     * @param ruleCode 规则标识（大小写敏感）
     * @return 对应的 Cleaner，不存在返回 null
     */
    public IDataCleaner get(String ruleCode) {
        return cleanerMap.get(ruleCode);
    }

    /**
     * 检查指定 ruleCode 是否有对应的 Cleaner
     */
    public boolean has(String ruleCode) {
        return cleanerMap.containsKey(ruleCode);
    }

    /**
     * 获取所有已注册的 ruleCode
     */
    public Map<String, IDataCleaner> getAll() {
        return new ConcurrentHashMap<>(cleanerMap);
    }
}