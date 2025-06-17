package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备记录值实体类（原始数据）
 * 
 * @author system
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_readings")
public class DeviceReading extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 读数时间
     */
    private LocalDateTime readingTime;

    /**
     * 当前读数值
     */
    private BigDecimal currentValue;

    /**
     * 增量值(与上次读数的差值)
     */
    private BigDecimal incrementValue;

    /**
     * 信号强度(0-100)
     */
    private Integer signalStrength;

    /**
     * 电池电量(0-100)
     */
    private Integer batteryLevel;

    /**
     * 数据质量(0:异常,1:正常,2:估算)
     */
    private Integer dataQuality;

    /**
     * 采集方式(auto:自动,manual:手工)
     */
    private String collectMethod;

    // 电表特有字段
    /**
     * 费率类型(peak:峰,flat:平,valley:谷) - 仅电表
     */
    private String rateType;

    /**
     * 功率因数 - 仅电表
     */
    private BigDecimal powerFactor;

    // 热表特有字段
    /**
     * 供水温度(℃) - 仅热表
     */
    private BigDecimal supplyTemp;

    /**
     * 回水温度(℃) - 仅热表
     */
    private BigDecimal returnTemp;

    /**
     * 瞬时流量 - 仅热表
     */
    private BigDecimal flowRate;

    // 气表特有字段
    /**
     * 压力值(MPa) - 仅气表
     */
    private BigDecimal pressure;

    /**
     * 温度值(℃) - 仅气表
     */
    private BigDecimal temperature;

    /**
     * 逻辑删除标记(0:正常,1:删除)
     */
    @TableLogic
    private Integer deleted;
}