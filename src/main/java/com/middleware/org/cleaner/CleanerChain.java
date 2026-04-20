package com.middleware.org.cleaner;

import com.middleware.org.entity.TaskCleanRule;
import com.middleware.org.exception.DataCleanException;
import com.middleware.org.model.ProcessedData;
import com.middleware.org.model.TaskContext;
import com.middleware.org.repository.TaskCleanRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 动态清洗链
 *
 * 执行流程：
 * 1. 根据 taskId 查询关联的规则列表（从 task_clean_rule 表）
 * 2. 按 exec_order 排序
 * 3. 遍历 ruleCode，从 CleanerFactory 获取 Cleaner
 * 4. 执行 clean()
 */
@Component
public class CleanerChain {

    private static final Logger log = LoggerFactory.getLogger(CleanerChain.class);

    @Autowired
    private CleanerFactory cleanerFactory;

    @Autowired
    private TaskCleanRuleRepository taskCleanRuleRepository;

    /**
     * 执行动态清洗流程
     *
     * @param processedData 待清洗数据
     * @param taskContext   任务上下文（从中获取 taskId）
     */
    public void clean(ProcessedData processedData, TaskContext taskContext) {
        String taskId = taskContext.getTaskId();

        // 1. 查询该任务关联的规则
        List<TaskCleanRule> taskRules = taskCleanRuleRepository.findByTaskIdOrderByExecOrder(taskId);

        if (taskRules == null || taskRules.isEmpty()) {
            log.info("任务 [{}] 未配置清洗规则，跳过清洗", taskId);
            return;
        }

        log.info("任务 [{}] 开始执行 [{}] 条清洗规则", taskId, taskRules.size());

        // 2. 按顺序遍历执行
        for (TaskCleanRule taskRule : taskRules) {
            String ruleCode = taskRule.getRuleCode();
            IDataCleaner cleaner = cleanerFactory.get(ruleCode);

            if (cleaner == null) {
                log.warn("任务 [{}] 规则 [{}] 未找到对应 Cleaner，跳过", taskId, ruleCode);
                continue;
            }

            try {
                log.debug("任务 [{}] 执行清洗规则: [{}] {}", taskId, ruleCode, cleaner.getName());
                cleaner.clean(processedData, taskContext);
                log.debug("任务 [{}] 清洗规则 [{}] 执行完成", taskId, ruleCode);
            } catch (Exception e) {
                log.error("任务 [{}] 清洗规则 [{}] 执行失败: {}", taskId, ruleCode, e.getMessage());
                throw new DataCleanException("清洗规则 [" + ruleCode + "] 执行失败: " + e.getMessage(), e);
            }
        }

        log.info("任务 [{}] 全部 {} 条清洗规则执行完成", taskId, taskRules.size());
    }

    /**
     * 兼容旧调用（无 taskContext 参数）
     * 此时不做任何清洗，保证向后兼容
     */
    public void clean(ProcessedData processedData) {
        // 默认空实现，保留向后兼容
    }

    /**
     * 标准化（向后兼容，原有固定逻辑）
     */
}