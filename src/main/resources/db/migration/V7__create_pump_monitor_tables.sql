-- 泵房自动化监控系统数据库表结构
-- 创建时间：2025-01-01
-- 描述：包含水泵设备、流量监控、液位监控三个核心表

-- 1. 水泵设备表
CREATE TABLE `pump_device` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_code` varchar(50) NOT NULL COMMENT '设备编码',
    `device_name` varchar(100) NOT NULL COMMENT '设备名称',
    `region_id` bigint DEFAULT NULL COMMENT '区域ID',
    `model` varchar(100) DEFAULT NULL COMMENT '设备型号',
    `manufacturer` varchar(100) DEFAULT NULL COMMENT '制造商',
    `rated_power` decimal(10,2) DEFAULT NULL COMMENT '额定功率(kW)',
    `rated_flow` decimal(10,2) DEFAULT NULL COMMENT '额定流量(m³/h)',
    `rated_head` decimal(10,2) DEFAULT NULL COMMENT '额定扬程(m)',
    `online_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '在线状态(0:离线,1:在线)',
    `work_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '工作状态(0:停止,1:运行,2:故障)',
    `alarm_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '报警状态(0:正常,1:报警)',
    `current_frequency` decimal(8,2) DEFAULT NULL COMMENT '当前频率(Hz)',
    `current_a` decimal(10,2) DEFAULT NULL COMMENT 'A相电流(A)',
    `current_b` decimal(10,2) DEFAULT NULL COMMENT 'B相电流(A)',
    `current_c` decimal(10,2) DEFAULT NULL COMMENT 'C相电流(A)',
    `voltage` decimal(8,2) DEFAULT NULL COMMENT '电压(V)',
    `positive_pressure` decimal(10,3) DEFAULT NULL COMMENT '正压值(MPa)',
    `negative_pressure` decimal(10,3) DEFAULT NULL COMMENT '负压值(MPa)',
    `power` decimal(8,2) DEFAULT NULL COMMENT '功率(kW)',
    `running_hours` decimal(10,2) DEFAULT 0.00 COMMENT '运行时长(小时)',
    `last_online_time` datetime DEFAULT NULL COMMENT '最后在线时间',
    `device_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态(0:停用,1:启用)',
    `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_code` (`device_code`),
    KEY `idx_region_id` (`region_id`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_work_status` (`work_status`),
    KEY `idx_alarm_status` (`alarm_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水泵设备表';

-- 2. 流量监控表
CREATE TABLE `flow_monitor` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_id` bigint NOT NULL COMMENT '设备ID',
    `device_code` varchar(50) NOT NULL COMMENT '设备编码',
    `instant_flow` decimal(10,3) DEFAULT NULL COMMENT '瞬时流量(m³/h)',
    `cumulative_flow` decimal(15,3) DEFAULT NULL COMMENT '累计流量(m³)',
    `flow_velocity` decimal(8,3) DEFAULT NULL COMMENT '流速(m/s)',
    `pressure` decimal(8,3) DEFAULT NULL COMMENT '压力(MPa)',
    `temperature` decimal(6,2) DEFAULT NULL COMMENT '温度(℃)',
    `monitor_time` datetime NOT NULL COMMENT '监测时间',
    `data_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '数据状态(0:异常,1:正常)',
    `alarm_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '报警状态(0:正常,1:报警)',
    `alarm_message` varchar(200) DEFAULT NULL COMMENT '报警信息',
    `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    KEY `idx_device_id` (`device_id`),
    KEY `idx_device_code` (`device_code`),
    KEY `idx_monitor_time` (`monitor_time`),
    KEY `idx_alarm_status` (`alarm_status`),
    CONSTRAINT `fk_flow_monitor_device` FOREIGN KEY (`device_id`) REFERENCES `pump_device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量监控表';

-- 3. 液位监控表
CREATE TABLE `level_monitor` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_id` bigint NOT NULL COMMENT '设备ID',
    `device_code` varchar(50) NOT NULL COMMENT '设备编码',
    `current_level` decimal(8,3) DEFAULT NULL COMMENT '当前液位(m)',
    `level_percentage` decimal(5,2) DEFAULT NULL COMMENT '液位百分比(%)',
    `total_height` decimal(8,3) DEFAULT NULL COMMENT '容器总高度(m)',
    `total_capacity` decimal(12,3) DEFAULT NULL COMMENT '容器总容量(m³)',
    `current_capacity` decimal(12,3) DEFAULT NULL COMMENT '当前容量(m³)',
    `liquid_temperature` decimal(6,2) DEFAULT NULL COMMENT '液体温度(℃)',
    `liquid_density` decimal(8,3) DEFAULT NULL COMMENT '液体密度(kg/m³)',
    `monitor_time` datetime NOT NULL COMMENT '监测时间',
    `data_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '数据状态(0:异常,1:正常)',
    `alarm_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '报警状态(0:正常,1:高液位报警,2:低液位报警)',
    `alarm_message` varchar(200) DEFAULT NULL COMMENT '报警信息',
    `high_alarm_threshold` decimal(5,2) DEFAULT 90.00 COMMENT '高液位报警阈值(%)',
    `low_alarm_threshold` decimal(5,2) DEFAULT 10.00 COMMENT '低液位报警阈值(%)',
    `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    KEY `idx_device_id` (`device_id`),
    KEY `idx_device_code` (`device_code`),
    KEY `idx_monitor_time` (`monitor_time`),
    KEY `idx_alarm_status` (`alarm_status`),
    CONSTRAINT `fk_level_monitor_device` FOREIGN KEY (`device_id`) REFERENCES `pump_device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='液位监控表';

-- 创建索引优化查询性能
CREATE INDEX `idx_flow_device_time` ON `flow_monitor` (`device_id`, `monitor_time`);
CREATE INDEX `idx_level_device_time` ON `level_monitor` (`device_id`, `monitor_time`);
CREATE INDEX `idx_pump_status` ON `pump_device` (`online_status`, `work_status`, `alarm_status`);