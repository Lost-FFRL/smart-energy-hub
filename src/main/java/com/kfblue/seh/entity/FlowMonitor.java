package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流量监控数据实体类
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_monitor")
public class FlowMonitor extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 瞬时流量(m³/h)
     */
    private BigDecimal instantFlow;

    /**
     * 累计流量(m³)
     */
    private BigDecimal totalFlow;

    /**
     * 流速(m/s)
     */
    private BigDecimal velocity;

    /**
     * 压力(MPa)
     */
    private BigDecimal pressure;

    /**
     * 温度(℃)
     */
    private BigDecimal temperature;

    /**
     * 监测时间
     */
    private LocalDateTime monitorTime;

    /**
     * 数据状态(0:异常,1:正常)
     */
    private Integer dataStatus;

    /**
     * 报警状态(0:正常,1:流量过高,2:流量过低,3:压力异常)
     */
    private Integer alarmStatus;

    /**
     * 报警信息
     */
    private String alarmMessage;
}