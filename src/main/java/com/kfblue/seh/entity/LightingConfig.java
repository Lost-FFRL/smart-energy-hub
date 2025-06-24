package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 智能控制配置实体类
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lighting_config")
public class LightingConfig extends BaseEntity {

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 控制模式(longitude:经纬度,manual:手动,timer:定时)
     */
    private String controlMode;

    /**
     * 所属区域ID(null表示全局配置)
     */
    private Long regionId;

    /**
     * 设备类型(null表示所有设备类型)
     */
    private String deviceType;

    /**
     * 是否启用(0:禁用,1:启用)
     */
    private Integer isEnabled;

    /**
     * 时段配置(JSON格式,包含多个时间段的开关灯配置)
     */
    private String timePeriods;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 目标设备列表(JSON格式,存储设备编码数组)
     */
    private String targetDevices;
}