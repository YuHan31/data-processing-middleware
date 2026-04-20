package com.middleware.org.service;

import com.middleware.org.entity.CleanRule;

import java.util.List;

/**
 * 清洗规则服务接口
 */
public interface ICleanRuleService {

    /**
     * 获取所有已启用的规则
     */
    List<CleanRule> getAllEnabledRules();

    /**
     * 切换规则启用状态
     */
    void toggleEnabled(Long id);

    /**
     * 批量保存任务规则关联
     */
    void saveTaskRules(String taskId, List<String> ruleCodes);

    /**
     * 查询任务关联的规则代码列表
     */
    List<String> getTaskRuleCodes(String taskId);
}