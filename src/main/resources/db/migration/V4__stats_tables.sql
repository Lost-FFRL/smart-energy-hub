-- 能耗管理系统数据库设计
-- 基于MySQL 8.0+

-- 1. 区域表
CREATE TABLE `regions`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `region_code`  varchar(50)  NOT NULL COMMENT '区域编码',
    `region_name`  varchar(100) NOT NULL COMMENT '区域名称',
    `parent_id`    bigint                DEFAULT NULL COMMENT '父级区域ID',
    `region_level` tinyint      NOT NULL DEFAULT 1 COMMENT '区域层级(1:一级,2:二级,3:三级等)',
    `region_path`  varchar(500)          DEFAULT NULL COMMENT '区域路径,用/分隔',
    `area_size`    decimal(10, 2)        DEFAULT NULL COMMENT '区域面积(平方米)',
    `region_type`  varchar(20)           DEFAULT NULL COMMENT '区域类型(office:办公,workshop:车间,dormitory:宿舍等)',
    `status`       tinyint(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    `created_by`   varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`   varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`      tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`       varchar(500)          DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_region_code` (`region_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_region_level` (`region_level`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='区域表';

-- 2. 设备表
CREATE TABLE `devices`
(
    `id`                     bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_code`            varchar(50)  NOT NULL COMMENT '设备编码',
    `device_name`            varchar(100) NOT NULL COMMENT '设备名称',
    `device_type`            varchar(20)  NOT NULL COMMENT '设备类型(water:水表,electric:电表,gas:气表,heat:热表)',
    `region_id`              bigint       NOT NULL COMMENT '所属区域ID',
    `device_model`           varchar(50)           DEFAULT NULL COMMENT '设备型号',
    `manufacturer`           varchar(100)          DEFAULT NULL COMMENT '生产厂商',
    `install_date`           date                  DEFAULT NULL COMMENT '安装日期',
    `device_addr`            varchar(100)          DEFAULT NULL COMMENT '设备地址',
    `communication_protocol` varchar(50)           DEFAULT NULL COMMENT '通信协议(modbus,mqtt,http等)',
    `collect_interval`       int                   DEFAULT 60 COMMENT '采集间隔(秒)',
    `unit`                   varchar(20)           DEFAULT NULL COMMENT '计量单位(kWh,m³,t等)',
    `precision_digits`       tinyint               DEFAULT 2 COMMENT '精度位数',
    `multiplier`             decimal(10, 4)        DEFAULT 1.0000 COMMENT '倍率系数',
    `initial_value`          decimal(15, 4)        DEFAULT 0.0000 COMMENT '初始值',
    `online_status`          tinyint(1)   NOT NULL DEFAULT 0 COMMENT '在线状态(0:离线,1:在线)',
    `last_online_time`       datetime              DEFAULT NULL COMMENT '最后在线时间',
    `status`                 tinyint(1)   NOT NULL DEFAULT 1 COMMENT '设备状态(0:停用,1:正常,2:维修)',
    `created_by`             varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`             datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`             varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`             datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`                tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`                 varchar(500)          DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_code` (`device_code`),
    KEY `idx_device_type` (`device_type`),
    KEY `idx_region_id` (`region_id`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_devices_region` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='设备表';

-- 3. 设备记录值表（原始数据）
CREATE TABLE `device_readings`
(
    `id`              bigint         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_id`       bigint         NOT NULL COMMENT '设备ID',
    `reading_time`    datetime       NOT NULL COMMENT '读数时间',
    `current_value`   decimal(15, 4) NOT NULL COMMENT '当前读数值',
    `increment_value` decimal(15, 4)          DEFAULT NULL COMMENT '增量值(与上次读数的差值)',
    `signal_strength` tinyint                 DEFAULT NULL COMMENT '信号强度(0-100)',
    `battery_level`   tinyint                 DEFAULT NULL COMMENT '电池电量(0-100)',
    `data_quality`    tinyint(1)              DEFAULT 1 COMMENT '数据质量(0:异常,1:正常,2:估算)',
    `collect_method`  varchar(20)             DEFAULT 'auto' COMMENT '采集方式(auto:自动,manual:手工)',
    -- 电表特有字段
    `rate_type`       varchar(10)             DEFAULT NULL COMMENT '费率类型(peak:峰,flat:平,valley:谷) - 仅电表',
    `power_factor`    decimal(5, 3)           DEFAULT NULL COMMENT '功率因数 - 仅电表',
    -- 热表特有字段
    `supply_temp`     decimal(6, 2)           DEFAULT NULL COMMENT '供水温度(℃) - 仅热表',
    `return_temp`     decimal(6, 2)           DEFAULT NULL COMMENT '回水温度(℃) - 仅热表',
    `flow_rate`       decimal(10, 4)          DEFAULT NULL COMMENT '瞬时流量 - 仅热表',
    -- 气表特有字段
    `pressure`        decimal(8, 4)           DEFAULT NULL COMMENT '压力值(MPa) - 仅气表',
    `temperature`     decimal(6, 2)           DEFAULT NULL COMMENT '温度值(℃) - 仅气表',
    `created_by`      varchar(64)             DEFAULT NULL COMMENT '创建人',
    `created_at`      datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`      varchar(64)             DEFAULT NULL COMMENT '修改人',
    `updated_at`      datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`         tinyint(1)     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`          varchar(500)            DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    KEY `idx_device_reading_time` (`device_id`, `reading_time`),
    KEY `idx_reading_time` (`reading_time`),
    KEY `idx_data_quality` (`data_quality`),
    CONSTRAINT `fk_readings_device` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='设备记录值表';

-- 4. 设备日统计表（预聚合数据）
CREATE TABLE `device_daily_statistics`
(
    `id`                   bigint     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_id`            bigint     NOT NULL COMMENT '设备ID',
    `stat_date`            date       NOT NULL COMMENT '统计日期',
    `start_value`          decimal(15, 4)      DEFAULT NULL COMMENT '日开始值',
    `end_value`            decimal(15, 4)      DEFAULT NULL COMMENT '日结束值',
    `daily_consumption`    decimal(15, 4)      DEFAULT NULL COMMENT '日用量',
    `peak_value`           decimal(15, 4)      DEFAULT NULL COMMENT '日峰值',
    `peak_time`            time                DEFAULT NULL COMMENT '峰值时间',
    `valley_value`         decimal(15, 4)      DEFAULT NULL COMMENT '日谷值',
    `valley_time`          time                DEFAULT NULL COMMENT '谷值时间',
    `avg_value`            decimal(15, 4)      DEFAULT NULL COMMENT '日均值',
    `reading_count`        int                 DEFAULT 0 COMMENT '读数次数',
    `normal_count`         int                 DEFAULT 0 COMMENT '正常读数次数',
    `abnormal_count`       int                 DEFAULT 0 COMMENT '异常读数次数',
    `data_integrity`       decimal(5, 2)       DEFAULT 100.00 COMMENT '数据完整性(%)',
    `cost_amount`          decimal(10, 2)      DEFAULT NULL COMMENT '费用金额(元)',
    `unit_price`           decimal(8, 4)       DEFAULT NULL COMMENT '单价(元/单位)',
    -- 电表特有统计字段
    `peak_consumption`     decimal(15, 4)      DEFAULT NULL COMMENT '峰时用电量 - 仅电表',
    `flat_consumption`     decimal(15, 4)      DEFAULT NULL COMMENT '平时用电量 - 仅电表',
    `valley_consumption`   decimal(15, 4)      DEFAULT NULL COMMENT '谷时用电量 - 仅电表',
    `avg_power_factor`     decimal(5, 3)       DEFAULT NULL COMMENT '平均功率因数 - 仅电表',
    -- 热表特有统计字段
    `avg_supply_temp`      decimal(6, 2)       DEFAULT NULL COMMENT '平均供水温度 - 仅热表',
    `avg_return_temp`      decimal(6, 2)       DEFAULT NULL COMMENT '平均回水温度 - 仅热表',
    `avg_temp_diff`        decimal(6, 2)       DEFAULT NULL COMMENT '平均温差 - 仅热表',
    `total_flow`           decimal(15, 4)      DEFAULT NULL COMMENT '累计流量 - 仅热表',
    -- 气表特有统计字段
    `avg_pressure`         decimal(8, 4)       DEFAULT NULL COMMENT '平均压力 - 仅气表',
    `avg_temperature`      decimal(6, 2)       DEFAULT NULL COMMENT '平均温度 - 仅气表',
    `standard_consumption` decimal(15, 4)      DEFAULT NULL COMMENT '标况用气量 - 仅气表',
    `created_by`           varchar(64)         DEFAULT NULL COMMENT '创建人',
    `created_at`           datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`           varchar(64)         DEFAULT NULL COMMENT '修改人',
    `updated_at`           datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`              tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`               varchar(500)        DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_date` (`device_id`, `stat_date`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_daily_consumption` (`daily_consumption`),
    CONSTRAINT `fk_daily_stats_device` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='设备日统计表';

-- 5. 告警表
CREATE TABLE `alarms`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_id`        bigint       NOT NULL COMMENT '设备ID',
    `alarm_code`       varchar(50)  NOT NULL COMMENT '告警编码',
    `alarm_type`       varchar(50)  NOT NULL COMMENT '告警类型(offline:离线,threshold:阈值,malfunction:故障等)',
    `alarm_level`      tinyint      NOT NULL COMMENT '告警级别(1:低,2:中,3:高,4:严重)',
    `alarm_title`      varchar(200) NOT NULL COMMENT '告警标题',
    `alarm_content`    text COMMENT '告警内容描述',
    `alarm_value`      decimal(15, 4)        DEFAULT NULL COMMENT '告警值',
    `threshold_value`  decimal(15, 4)        DEFAULT NULL COMMENT '阈值',
    `alarm_time`       datetime     NOT NULL COMMENT '告警时间',
    `alarm_status`     tinyint(1)   NOT NULL DEFAULT 0 COMMENT '告警状态(0:未处理,1:处理中,2:已处理,3:已忽略)',
    `handle_user`      varchar(64)           DEFAULT NULL COMMENT '处理人',
    `handle_time`      datetime              DEFAULT NULL COMMENT '处理时间',
    `handle_result`    text                  DEFAULT NULL COMMENT '处理结果',
    `auto_clear`       tinyint(1)            DEFAULT 0 COMMENT '是否自动清除(0:否,1:是)',
    `clear_time`       datetime              DEFAULT NULL COMMENT '清除时间',
    `duration_minutes` int                   DEFAULT NULL COMMENT '持续时长(分钟)',
    `created_by`       varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`       varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`          tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`           varchar(500)          DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    KEY `idx_device_alarm_time` (`device_id`, `alarm_time`),
    KEY `idx_alarm_type` (`alarm_type`),
    KEY `idx_alarm_level` (`alarm_level`),
    KEY `idx_alarm_status` (`alarm_status`),
    KEY `idx_alarm_time` (`alarm_time`),
    CONSTRAINT `fk_alarms_device` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='告警表';

-- 创建索引优化查询性能
-- 设备记录值表额外索引（用于时间范围查询）
CREATE INDEX `idx_readings_device_time_range` ON `device_readings` (`device_id`, `reading_time`, `current_value`);

-- 告警表复合索引（用于统计查询）
CREATE INDEX `idx_alarms_stats` ON `alarms` (`alarm_time`, `alarm_level`, `alarm_status`);

-- 日统计表时间范围索引
CREATE INDEX `idx_daily_stats_time_range` ON `device_daily_statistics` (`stat_date`, `device_id`, `daily_consumption`);

-- 视图：设备在线状态统计
CREATE VIEW `v_device_online_stats` AS
SELECT d.device_type,
       r.region_name,
       COUNT(*)                                                                          as total_devices,
       SUM(CASE WHEN d.online_status = 1 THEN 1 ELSE 0 END)                              as online_devices,
       SUM(CASE WHEN d.online_status = 0 THEN 1 ELSE 0 END)                              as offline_devices,
       ROUND(SUM(CASE WHEN d.online_status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as online_rate
FROM devices d
         LEFT JOIN regions r ON d.region_id = r.id
WHERE d.deleted = 0
  AND d.status = 1
GROUP BY d.device_type, r.region_name;

-- 视图：今日能耗统计
CREATE VIEW `v_today_consumption` AS
SELECT d.device_type,
       r.region_name,
       COUNT(ds.id)              as device_count,
       SUM(ds.daily_consumption) as total_consumption,
       AVG(ds.daily_consumption) as avg_consumption,
       SUM(ds.cost_amount)       as total_cost
FROM device_daily_statistics ds
         LEFT JOIN devices d ON ds.device_id = d.id
         LEFT JOIN regions r ON d.region_id = r.id
WHERE ds.stat_date = CURDATE()
  AND d.deleted = 0
  AND d.status = 1
GROUP BY d.device_type, r.region_name;

-- 视图：告警统计
CREATE VIEW `v_alarm_stats` AS
SELECT d.device_type,
       r.region_name,
       a.alarm_level,
       a.alarm_status,
       COUNT(*)          as alarm_count,
       MIN(a.alarm_time) as first_alarm_time,
       MAX(a.alarm_time) as last_alarm_time
FROM alarms a
         LEFT JOIN devices d ON a.device_id = d.id
         LEFT JOIN regions r ON d.region_id = r.id
WHERE a.deleted = 0
  AND a.alarm_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY d.device_type, r.region_name, a.alarm_level, a.alarm_status;