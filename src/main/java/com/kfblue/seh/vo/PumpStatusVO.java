package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 水泵状态统计VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "水泵状态统计")
public class PumpStatusVO {
    
    // 设备基础信息
    @Schema(description = "设备ID")
    private Long deviceId;
    
    @Schema(description = "设备编码")
    private String deviceCode;
    
    @Schema(description = "设备名称")
    private String deviceName;
    
    @Schema(description = "设备类型")
    private Integer deviceType;
    
    @Schema(description = "设备型号")
    private String model;
    
    @Schema(description = "制造商")
    private String manufacturer;
    
    // 设备状态
    @Schema(description = "在线状态(0:离线,1:在线)")
    private Integer onlineStatus;
    
    @Schema(description = "工作状态(0:停止,1:运行)")
    private Integer workStatus;
    
    @Schema(description = "报警状态(0:正常,1:报警)")
    private Integer alarmStatus;
    
    // 设备运行参数
    @Schema(description = "电压(V)")
    private BigDecimal voltage;
    
    @Schema(description = "A相电流(A)")
    private BigDecimal currentA;
    
    @Schema(description = "B相电流(A)")
    private BigDecimal currentB;
    
    @Schema(description = "C相电流(A)")
    private BigDecimal currentC;
    
    @Schema(description = "正压(MPa)")
    private BigDecimal positivePressure;
    
    @Schema(description = "负压(MPa)")
    private BigDecimal negativePressure;
    
    // 设备规格参数
    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;
    
    @Schema(description = "额定流量(m³/h)")
    private BigDecimal ratedFlow;
    
    @Schema(description = "额定扬程(m)")
    private BigDecimal ratedHead;

}