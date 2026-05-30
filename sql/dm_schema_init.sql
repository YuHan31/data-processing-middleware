-- 使用 SYSDBA 用户登录达梦后执行本脚本。
-- 本项目不单独创建数据库用户，所有表放在 MIDDLEWARE_DB 模式下。

CREATE SCHEMA MIDDLEWARE_DB AUTHORIZATION SYSDBA;
SET SCHEMA MIDDLEWARE_DB;

CREATE TABLE users (
    id BIGINT IDENTITY(1,1) NOT NULL,
    account VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    name VARCHAR(50),
    nickname VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    user_role VARCHAR(32) DEFAULT 'USER' NOT NULL,
    enabled TINYINT DEFAULT 1 NOT NULL,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_account UNIQUE (account),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone UNIQUE (phone)
);

CREATE INDEX idx_users_role ON users (user_role);
CREATE INDEX idx_users_enabled ON users (enabled);

CREATE TABLE task (
    id BIGINT IDENTITY(1,1) NOT NULL,
    task_id VARCHAR(64) NOT NULL,
    task_name VARCHAR(256) DEFAULT '',
    user_id BIGINT,
    input_file_path VARCHAR(512) DEFAULT '',
    output_file_path VARCHAR(512) DEFAULT '',
    file_type VARCHAR(32) DEFAULT '',
    output_format VARCHAR(16) DEFAULT 'csv',
    status VARCHAR(32) DEFAULT 'UPLOADED',
    original_file_name VARCHAR(256) DEFAULT '',
    file_size BIGINT DEFAULT 0,
    upload_time BIGINT DEFAULT 0,
    start_time TIMESTAMP(0),
    end_time TIMESTAMP(0),
    processed_data_summary CLOB,
    statistics CLOB,
    original_records CLOB,
    cleaned_records CLOB,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id),
    CONSTRAINT uk_task_task_id UNIQUE (task_id)
);

CREATE INDEX idx_task_user_id ON task (user_id);
CREATE INDEX idx_task_status ON task (status);

CREATE TABLE clean_rule (
    id BIGINT IDENTITY(1,1) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    description VARCHAR(512) DEFAULT '',
    rule_type VARCHAR(32) NOT NULL,
    rule_level VARCHAR(32) DEFAULT 'basic',
    enabled TINYINT DEFAULT 1 NOT NULL,
    display_order INT DEFAULT 0 NOT NULL,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_clean_rule PRIMARY KEY (id),
    CONSTRAINT uk_clean_rule_code UNIQUE (rule_code)
);

CREATE INDEX idx_clean_rule_type ON clean_rule (rule_type);
CREATE INDEX idx_clean_rule_enabled ON clean_rule (enabled);

CREATE TABLE task_clean_rule (
    id BIGINT IDENTITY(1,1) NOT NULL,
    task_id VARCHAR(64) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    exec_order INT DEFAULT 0 NOT NULL,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_task_clean_rule PRIMARY KEY (id),
    CONSTRAINT uk_task_rule UNIQUE (task_id, rule_code)
);

CREATE INDEX idx_task_clean_rule_task_id ON task_clean_rule (task_id);

CREATE TABLE system_log (
    id BIGINT IDENTITY(1,1) NOT NULL,
    log_level VARCHAR(32) NOT NULL,
    message VARCHAR(2000) DEFAULT '',
    user_message VARCHAR(2000) DEFAULT '',
    task_id VARCHAR(64) NOT NULL,
    log_timestamp BIGINT DEFAULT 0,
    stage VARCHAR(32) DEFAULT '',
    exception_message VARCHAR(1000) DEFAULT '',
    stack_trace CLOB,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_system_log PRIMARY KEY (id)
);

CREATE INDEX idx_system_log_level ON system_log (log_level);
CREATE INDEX idx_system_log_task_id ON system_log (task_id);

INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('TRIM', '去除空格', '去除字段值的首尾空格', 'FORMAT', 'basic', 1, 10);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('TO_LOWER', '转小写', '将文本字段转为小写', 'FORMAT', 'basic', 1, 11);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('TO_UPPER', '转大写', '将文本字段转为大写', 'FORMAT', 'basic', 1, 12);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('REMOVE_NULL', '删除空值', '删除值为空或null的记录', 'NORMALIZE', 'basic', 1, 20);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('REMOVE_EMPTY_ROW', '删除空行', '删除所有字段都为空的记录', 'NORMALIZE', 'basic', 1, 21);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('DEDUPLICATE', '去除重复', '删除完全重复的记录', 'NORMALIZE', 'basic', 1, 22);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('DATA_MASK', '数据脱敏', '对敏感数据进行脱敏处理', 'SECURITY', 'advanced', 1, 30);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('PHONE_MASK', '手机号脱敏', '对手机号进行脱敏（显示前三位和后四位）', 'SECURITY', 'advanced', 1, 31);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('EMAIL_MASK', '邮箱脱敏', '对邮箱地址进行脱敏', 'SECURITY', 'advanced', 1, 32);
INSERT INTO clean_rule (rule_code, rule_name, description, rule_type, rule_level, enabled, display_order)
VALUES ('NORMALIZE_DATE', '日期标准化', '将日期统一为标准格式', 'NORMALIZE', 'advanced', 1, 40);

INSERT INTO users (account, email, phone, name, password, user_role, enabled, create_time, updated_at)
VALUES ('admin', 'admin@example.com', '13800000000', '系统管理员',
        '$2a$10$rNPGcJ5vLQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJx',
        'ADMIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
