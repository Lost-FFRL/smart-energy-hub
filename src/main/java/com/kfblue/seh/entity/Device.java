package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备实体类
 * 
 * @author system
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("devices")
public class Device extends BaseEntity {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型(water:水表,electric:电表,gas:气表,heat:热表)
     */
    private String deviceType;

    /**
     * 所属区域ID
     */
    private Long regionId;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 安装日期
     */
    private LocalDate installDate;

    /**
     * 设备地址
     */
    private String deviceAddr;

    /**
     * 通信协议(modbus,mqtt,http等)
     */
    private String communicationProtocol;

    /**
     * 采集间隔(秒)
     */
    private Integer collectInterval;

    /**
     * 计量单位(kWh,m³,t等)
     */
    private String unit;

    /**
     * 精度位数
     */
    private Integer precisionDigits;

    /**
     * 倍率系数
     */
    private BigDecimal multiplier;

    /**
     * 初始值
     */
    private BigDecimal initialValue;

    /**
     * 在线状态(0:离线,1:在线)
     */
    private Integer onlineStatus;

    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;

    /**
     * 设备状态(0:停用,1:正常,2:维修)
     */
    private Integer status;
}