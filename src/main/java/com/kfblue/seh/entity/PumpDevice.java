package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水泵设备实体类
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pump_device")
public class PumpDevice extends BaseEntity {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 所属区域ID
     */
    private Long regionId;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 额定功率(kW)
     */
    private BigDecimal ratedPower;

    /**
     * 额定流量(m³/h)
     */
    private BigDecimal ratedFlow;

    /**
     * 额定扬程(m)
     */
    private BigDecimal ratedHead;

    /**
     * 在线状态(0:离线,1:在线)
     */
    private Integer onlineStatus;

    /**
     * 工作状态(0:停止,1:运行,2:故障)
     */
    private Integer workStatus;

    /**
     * 报警状态(0:正常,1:报警)
     */
    private Integer alarmStatus;

    /**
     * 当前频率(Hz)
     */
    private BigDecimal currentFrequency;

    /**
     * A相电流(A)
     */
    private BigDecimal currentA;

    /**
     * B相电流(A)
     */
    private BigDecimal currentB;

    /**
     * C相电流(A)
     */
    private BigDecimal currentC;

    /**
     * 电压(V)
     */
    private BigDecimal voltage;

    /**
     * 正压值(MPa)
     */
    private BigDecimal positivePressure;

    /**
     * 负压值(MPa)
     */
    private BigDecimal negativePressure;

    /**
     * 功率(kW)
     */
    private BigDecimal power;

    /**
     * 运行时长(小时)
     */
    private BigDecimal runningHours;

    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;

    /**
     * 设备状态(0:停用,1:启用)
     */
    private Integer deviceStatus;
}