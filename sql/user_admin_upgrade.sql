-- ===============================================
-- 用户表结构升级：添加角色和启用状态字段
-- ===============================================

-- 添加 role 字段（默认普通用户）
ALTER TABLE `users` ADD COLUMN `role` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER-普通用户，ADMIN-管理员';

-- 添加 enabled 字段（默认启用）
ALTER TABLE `users` ADD COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户启用状态：1-启用 0-禁用';

-- 创建管理员索引
CREATE INDEX IF NOT EXISTS `idx_role` ON `users` (`role`);
CREATE INDEX IF NOT EXISTS `idx_enabled` ON `users` (`enabled`);

-- ===============================================
-- 插入默认管理员账户
-- 注意：密码是 admin123456 的 BCrypt 加密结果
-- ===============================================
-- BCrypt 哈希可通过以下 Java 代码生成：
-- new BCryptPasswordEncoder().encode("admin123456")
-- 临时使用下面的哈希值（admin123456）：
INSERT INTO `users` (`account`, `email`, `phone`, `name`, `password`, `role`, `enabled`, `create_time`, `updated_at`)
VALUES ('admin', 'admin@example.com', '13800000000', '系统管理员', '$2a$10$rNPGcJ5vLQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJxZx7LQJx', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `role` = 'ADMIN';