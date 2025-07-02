CREATE TABLE IF NOT EXISTS `gw_data`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `XGateway`   varchar(100) DEFAULT NULL COMMENT '网关标识',
    `XTagName`   varchar(100) DEFAULT NULL COMMENT '标签名称',
    `XValue`     varchar(255) DEFAULT NULL COMMENT '数据值',
    `XQuality`   tinyint      DEFAULT 0 COMMENT '数据质量(0:差,1:良好,2:优秀)',
    `XTimeStamp` timestamp    DEFAULT NULL COMMENT '时间戳',
    `CreateAt`   timestamp    DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
    PRIMARY KEY (`id`),
    KEY `idx_gateway_time` (`XGateway`, `XTimeStamp`),
    KEY `idx_tagname_time` (`XTagName`, `XTimeStamp`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='网关数据表';