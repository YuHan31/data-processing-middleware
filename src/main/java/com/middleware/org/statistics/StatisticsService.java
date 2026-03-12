package com.middleware.org.statistics;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计服务
 */
@Service
public class StatisticsService {

    private final Map<String, DataStatistics> statisticsCache = new ConcurrentHashMap<>();

    /**
     * 生成数据统计
     */
    public DataStatistics generateStatistics(TaskContext taskContext) {
        String taskId = taskContext.getTaskId();
        DataStatistics statistics = new DataStatistics(taskId);

        ProcessedData processedData = taskContext.getProcessedData();
        if (processedData == null) {
            return statistics;
        }

        List<DataRecord> records = processedData.getRecords();
        if (records == null || records.isEmpty()) {
            return statistics;
        }

        long totalRecords = records.size();
        long validRecords = records.stream().filter(DataRecord::isValid).count();
        long invalidRecords = totalRecords - validRecords;
        long missingValues = countMissingValues(records);

        statistics.setTotalRecords(totalRecords);
        statistics.setValidRecords(validRecords);
        statistics.setInvalidRecords(invalidRecords);
        statistics.setMissingValues(missingValues);
        statistics.setStartTime(DateUtil.getCurrentDateTime());
        statistics.setEndTime(DateUtil.getCurrentDateTime());

        statisticsCache.put(taskId, statistics);
        taskContext.setStatistics(statistics);

        return statistics;
    }

    /**
     * 统计缺失值数量
     */
    private long countMissingValues(List<DataRecord> records) {
        long count = 0;
        for (DataRecord record : records) {
            Map<String, Object> data = record.getData();
            if (data != null) {
                for (Object value : data.values()) {
                    if (value == null || value.toString().trim().isEmpty()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 获取统计信息
     */
    public DataStatistics getStatistics(String taskId) {
        return statisticsCache.get(taskId);
    }

    /**
     * 清除统计信息
     */
    public void clearStatistics(String taskId) {
        statisticsCache.remove(taskId);
    }
}
