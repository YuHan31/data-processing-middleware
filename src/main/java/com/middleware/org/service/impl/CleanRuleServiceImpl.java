package com.middleware.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.middleware.org.entity.CleanRule;
import com.middleware.org.entity.TaskCleanRule;
import com.middleware.org.repository.CleanRuleRepository;
import com.middleware.org.repository.TaskCleanRuleRepository;
import com.middleware.org.service.ICleanRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清洗规则服务实现
 */
@Service
public class CleanRuleServiceImpl implements ICleanRuleService {

    @Autowired
    private CleanRuleRepository cleanRuleRepository;

    @Autowired
    private TaskCleanRuleRepository taskCleanRuleRepository;

    @Override
    public List<CleanRule> getAllEnabledRules() {
        LambdaQueryWrapper<CleanRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CleanRule::getEnabled, true)
                .orderByAsc(CleanRule::getDisplayOrder);
        return cleanRuleRepository.selectList(wrapper);
    }

    @Override
    @Transactional
    public void toggleEnabled(Long id) {
        CleanRule rule = cleanRuleRepository.selectById(id);
        if (rule != null) {
            rule.setEnabled(!rule.getEnabled());
            cleanRuleRepository.updateById(rule);
        }
    }

    @Override
    @Transactional
    public void saveTaskRules(String taskId, List<String> ruleCodes) {
        taskCleanRuleRepository.deleteByTaskId(taskId);
        if (ruleCodes == null || ruleCodes.isEmpty()) {
            return;
        }
        for (int i = 0; i < ruleCodes.size(); i++) {
            TaskCleanRule tr = new TaskCleanRule(taskId, ruleCodes.get(i), i);
            taskCleanRuleRepository.insert(tr);
        }
    }

    @Override
    public List<String> getTaskRuleCodes(String taskId) {
        List<TaskCleanRule> rules = taskCleanRuleRepository.findByTaskIdOrderByExecOrder(taskId);
        return rules.stream()
                .map(TaskCleanRule::getRuleCode)
                .collect(Collectors.toList());
    }
}
