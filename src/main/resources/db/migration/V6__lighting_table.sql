-- 照明系统简化版数据库设计

-- 1. 照明设备表（简化版）
CREATE TABLE IF NOT EXISTS `lighting_devices`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `device_code`   varchar(50)  NOT NULL COMMENT '设备编码',
    `device_name`   varchar(100) NOT NULL COMMENT '设备名称',
    `region_id`     bigint       NOT NULL COMMENT '所属区域ID',
    `device_type`   varchar(20)  NOT NULL DEFAULT 'single_light' COMMENT '设备类型(single_light:单灯,light_box:灯箱,LED,荧光灯,白炽灯,智能灯)',
    `online_status` tinyint(1)   NOT NULL DEFAULT 0 COMMENT '在线状态(0:离线,1:在线)',
    `work_status`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT '工作状态(0:关闭,1:开启)',
    `alarm_status`  tinyint(1)   NOT NULL DEFAULT 0 COMMENT '告警状态(0:正常,1:告警)',

    -- 标准字段
    `created_by`    varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`    varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`       tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`        varchar(500)          DEFAULT NULL COMMENT '备注信息',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_code` (`device_code`),
    KEY `idx_region_id` (`region_id`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_work_status` (`work_status`),
    KEY `idx_alarm_status` (`alarm_status`),
    CONSTRAINT `fk_lighting_devices_region` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='照明设备表';


-- 插入示例数据
INSERT INTO `lighting_devices` (`device_code`, `device_name`, `region_id`, `device_type`, `online_status`,
                                `work_status`, `alarm_status`, `created_by`)
VALUES ('BOX001', '办公区配电箱', 1, 'light_box', 1, 1, 0, 'system'),
       ('BOX002', '会议室配电箱', 1, 'light_box', 1, 1, 0, 'system'),
       ('LED001', '办公室主灯', 1, 'single_light', 1, 0, 0, 'system'),
       ('LED002', '会议室灯光', 1, 'single_light', 1, 1, 0, 'system'),
       ('LED003', '走廊照明', 1, 'single_light', 0, 0, 1, 'system'),
       ('LED004', '前台智能灯', 1, 'single_light', 1, 1, 0, 'system'),
       ('LED005', '大厅吊灯', 1, 'single_light', 1, 0, 0, 'system'),
       ('BOX003', '走廊配电箱', 1, 'light_box', 1, 1, 1, 'system');

-- 2. 智能控制配置表
CREATE TABLE IF NOT EXISTS `lighting_config`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `config_name`    varchar(100) NOT NULL COMMENT '配置名称',
    `control_mode`   varchar(20)  NOT NULL DEFAULT 'longitude' COMMENT '控制模式(longitude:经纬度,manual:手动,timer:定时)',
    `region_id`      bigint                DEFAULT NULL COMMENT '所属区域ID(null表示全局配置)',
    `device_type`    varchar(20)           DEFAULT NULL COMMENT '设备类型(null表示所有设备类型)',
    `is_enabled`     tinyint(1)   NOT NULL DEFAULT 1 COMMENT '是否启用(0:禁用,1:启用)',

    -- 时段配置
    `time_periods`   text                  DEFAULT NULL COMMENT '时段配置(JSON格式,包含多个时间段的开关灯配置)',

    -- 经纬度配置
    `longitude`      decimal(10, 7)        DEFAULT NULL COMMENT '经度',
    `latitude`       decimal(10, 7)        DEFAULT NULL COMMENT '纬度',

    -- 设备控制配置
    `target_devices` text                  DEFAULT NULL COMMENT '目标设备列表(JSON格式,存储设备编码数组)',

    -- 标准字段
    `created_by`     varchar(64)           DEFAULT NULL COMMENT '创建人',
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`     varchar(64)           DEFAULT NULL COMMENT '修改人',
    `updated_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`        tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0:正常,1:删除)',
    `remark`         varchar(500)          DEFAULT NULL COMMENT '备注信息',

    PRIMARY KEY (`id`),
    KEY `idx_control_mode` (`control_mode`),
    KEY `idx_region_id` (`region_id`),
    KEY `idx_device_type` (`device_type`),
    KEY `idx_is_enabled` (`is_enabled`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='智能控制配置表';

-- 插入智能控制配置示例数据
INSERT INTO `lighting_config` (`config_name`, `control_mode`, `region_id`, `device_type`, `time_periods`,
                               `target_devices`, `created_by`, `remark`)
VALUES ('办公区开关灯计划', 'timer', 1, 'single_light',
        '[{"startTime":"19:39:00","endTime":"06:20:00","action":"on","description":"夜间照明时段"},{"startTime":"06:20:00","endTime":"19:39:00","action":"off","description":"白天关闭时段"}]',
        '["LED001","LED002","LED003","LED004","LED005"]', 'system', '办公区域定时开关灯控制'),
       ('配电箱控制计划', 'timer', 1, 'light_box',
        '[{"startTime":"18:00:00","endTime":"08:00:00","action":"on","description":"夜间供电时段"},{"startTime":"08:00:00","endTime":"18:00:00","action":"off","description":"白天节能时段"}]',
        '["BOX001","BOX002","BOX003"]', 'system', '配电箱定时控制'),
       ('全局经纬度控制', 'longitude', NULL, NULL,
        '[{"startTime":"18:00:00","endTime":"08:00:00","action":"on","description":"夜间供电时段"},{"startTime":"08:00:00","endTime":"18:00:00","action":"off","description":"白天节能时段"}]',
        NULL, 'system', '基于经纬度的全局照明控制');