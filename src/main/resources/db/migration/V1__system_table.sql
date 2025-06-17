-- 创建用户表
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `username` varchar(50) NOT NULL COMMENT '用户名',
                            `password` varchar(100) NOT NULL COMMENT '密码',
                            `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
                            `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                            `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                            `gender` tinyint(1) DEFAULT NULL COMMENT '性别：0-女，1-男',
                            `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
                            `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                            `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
                            `role_id` bigint DEFAULT NULL COMMENT '角色ID',
                            `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                            `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
                            `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
                            `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`),
                            UNIQUE KEY `uk_phone` (`phone`),
                            UNIQUE KEY `uk_email` (`email`),
                            KEY `idx_status` (`status`),
                            KEY `idx_dept_id` (`dept_id`),
                            KEY `idx_role_id` (`role_id`),
                            KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认管理员用户（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `status`, `created_by`, `updated_by`)
VALUES ('admin', '$2a$10$7JB720yubVSOfvVWbazBuOWShWvheWjxVYaGYoUaxMNh4qDql5KLO', '系统管理员', 1, 'system', 'system');
