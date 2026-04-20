-- ===============================================
-- 清洗规则表 & 任务规则关联表
-- ===============================================

-- 1. 清洗规则表（规则库）
CREATE TABLE IF NOT EXISTS `clean_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `rule_code` VARCHAR(64) NOT NULL UNIQUE COMMENT '规则唯一标识',
    `rule_name` VARCHAR(128) NOT NULL COMMENT '展示名称',
    `description` VARCHAR(512) DEFAULT '' COMMENT '规则说明',
    `rule_type` VARCHAR(32) NOT NULL COMMENT '分类：FORMAT / NORMALIZE / SECURITY',
    `level` VARCHAR(32) DEFAULT 'basic' COMMENT '级别：basic / advanced',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用 0-禁用',
    `display_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_rule_type` (`rule_type`),
    INDEX `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='清洗规则表';

-- 2. 任务-规则关联表
CREATE TABLE IF NOT EXISTS `task_clean_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `task_id` VARCHAR(64) NOT NULL COMMENT '任务ID',
    `rule_code` VARCHAR(64) NOT NULL COMMENT '规则标识',
    `exec_order` INT NOT NULL DEFAULT 0 COMMENT '执行顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_task_id` (`task_id`),
    UNIQUE KEY `uk_task_rule` (`task_id`, `rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务规则关联表';

-- ===============================================
-- 预置规则数据
-- ===============================================
INSERT INTO `clean_rule` (`rule_code`, `rule_name`, `description`, `rule_type`, `level`, `enabled`, `display_order`) VALUES
-- FORMAT 类型（基础）
('TRIM',            '去除空格',      '去除字段值的首尾空格',                  'FORMAT',  'basic',    1, 10),
('TO_LOWER',        '转小写',         '将文本字段转为小写',                    'FORMAT',  'basic',    1, 11),
('TO_UPPER',        '转大写',         '将文本字段转为大写',                    'FORMAT',  'basic',    1, 12),
-- NORMALIZE 类型（基础）
('REMOVE_NULL',     '删除空值',       '删除值为空或null的记录',               'NORMALIZE','basic',    1, 20),
('REMOVE_EMPTY_ROW','删除空行',       '删除所有字段都为空的记录',             'NORMALIZE','basic',    1, 21),
('DEDUPLICATE',     '去除重复',       '删除完全重复的记录',                   'NORMALIZE','basic',    1, 22),
-- SECURITY 类型（基础）
('DATA_MASK',       '数据脱敏',       '对敏感数据进行脱敏处理',               'SECURITY', 'advanced', 1, 30),
-- SECURITY 类型（高级）
('PHONE_MASK',      '手机号脱敏',     '对手机号进行脱敏（显示前三位和后四位）', 'SECURITY', 'advanced', 1, 31),
('EMAIL_MASK',      '邮箱脱敏',       '对邮箱地址进行脱敏',                    'SECURITY', 'advanced', 1, 32),
('NORMALIZE_DATE',  '日期标准化',     '将日期统一为标准格式',                 'NORMALIZE', 'advanced', 1, 40);