package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水泵设备VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "水泵设备信息")
public class PumpDeviceVO {

    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "所属区域ID")
    private Long regionId;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "生产厂商")
    private String manufacturer;

    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;

    @Schema(description = "额定流量(m³/h)")
    private BigDecimal ratedFlow;

    @Schema(description = "额定扬程(m)")
    private BigDecimal ratedHead;

    @Schema(description = "在线状态(0:离线,1:在线)")
    private Integer onlineStatus;

    @Schema(description = "工作状态(0:停止,1:运行,2:故障)")
    private Integer workStatus;

    @Schema(description = "报警状态(0:正常,1:报警)")
    private Integer alarmStatus;

    @Schema(description = "当前频率(Hz)")
    private BigDecimal currentFrequency;

    @Schema(description = "A相电流(A)")
    private BigDecimal currentA;

    @Schema(description = "B相电流(A)")
    private BigDecimal currentB;

    @Schema(description = "C相电流(A)")
    private BigDecimal currentC;

    @Schema(description = "电压(V)")
    private BigDecimal voltage;

    @Schema(description = "功率(kW)")
    private BigDecimal power;

    @Schema(description = "运行时长(小时)")
    private BigDecimal runningHours;

    @Schema(description = "最后在线时间")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "设备状态(0:停用,1:启用)")
    private Integer deviceStatus;

}