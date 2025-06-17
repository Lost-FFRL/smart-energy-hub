-- 创建设备数据表
CREATE TABLE IF NOT EXISTS `device_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `XGateway` varchar(100) DEFAULT NULL COMMENT '网关',
  `XTagName` varchar(100) NOT NULL COMMENT '标签名称',
  `XValue` varchar(500) NOT NULL COMMENT '值',
  `XQuality` varchar(50) NOT NULL  COMMENT '质量戳',
  `XTimeStamp` varchar(50) NOT NULL COMMENT '时间戳',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`),
  KEY `idx_gateway` (`XGateway`),
  KEY `idx_tag_name` (`XTagName`),
  KEY `idx_timestamp` (`XTimeStamp`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_gateway_tag` (`XGateway`, `XTagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备数据表';