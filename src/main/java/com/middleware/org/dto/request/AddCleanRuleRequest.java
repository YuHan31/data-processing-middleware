package com.middleware.org.dto.request;

import lombok.Data;

/**
 * 添加/更新清洗规则请求
 */
@Data
public class AddCleanRuleRequest {
    private String ruleCode;
    private String ruleName;
    private String description;
    private String ruleType;
    private String level;
    private Boolean enabled;
    private Integer displayOrder;
}
