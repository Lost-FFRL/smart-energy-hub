package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 液位监控数据实体类
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("level_monitor")
public class LevelMonitor extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 当前液位(m)
     */
    private BigDecimal currentLevel;

    /**
     * 液位百分比(%)
     */
    private BigDecimal levelPercentage;

    /**
     * 容器总高度(m)
     */
    private BigDecimal totalHeight;

    /**
     * 容器总容量(m³)
     */
    private BigDecimal totalCapacity;

    /**
     * 当前容量(m³)
     */
    private BigDecimal currentCapacity;

    /**
     * 液体温度(℃)
     */
    private BigDecimal temperature;

    /**
     * 液体密度(kg/m³)
     */
    private BigDecimal density;

    /**
     * 监测时间
     */
    private LocalDateTime monitorTime;

    /**
     * 数据状态(0:异常,1:正常)
     */
    private Integer dataStatus;

    /**
     * 报警状态(0:正常,1:高液位,2:低液位,3:超高液位,4:超低液位)
     */
    private Integer alarmStatus;

    /**
     * 报警信息
     */
    private String alarmMessage;

    /**
     * 高液位报警阈值(m)
     */
    private BigDecimal highAlarmThreshold;

    /**
     * 低液位报警阈值(m)
     */
    private BigDecimal lowAlarmThreshold;
}