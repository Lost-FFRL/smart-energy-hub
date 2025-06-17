-- 创建demo表
CREATE TABLE IF NOT EXISTS `demo`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        varchar(100) NOT NULL COMMENT '名称',
    `description` varchar(500)          DEFAULT NULL COMMENT '描述',
    `status`      tinyint(1)   NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `sort_order`  int                   DEFAULT '0' COMMENT '排序',
    `created_by`  varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`  varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`     tinyint(1)   NOT NULL DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='Demo表';

-- 插入demo表测试数据
INSERT INTO `demo` (`name`, `description`, `status`, `sort_order`, `created_by`, `updated_by`, `remark`)
VALUES ('测试数据1', '这是第一条测试数据', 1, 1, 'system', 'system', '测试备注1'),
       ('测试数据2', '这是第二条测试数据', 1, 2, 'system', 'system', '测试备注2'),
       ('测试数据3', '这是第三条测试数据', 0, 3, 'system', 'system', '测试备注3'),
       ('测试数据4', '这是第四条测试数据', 1, 4, 'system', 'system', '测试备注4'),
       ('测试数据5', '这是第五条测试数据', 1, 5, 'system', 'system', '测试备注5');