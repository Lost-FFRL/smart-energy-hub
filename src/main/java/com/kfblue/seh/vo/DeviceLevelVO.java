package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备液位数据VO
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@Schema(description = "设备液位数据")
public class DeviceLevelVO {
    
    @Schema(description = "设备ID")
    private Long deviceId;
    
    @Schema(description = "设备编码")
    private String deviceCode;
    
    @Schema(description = "设备名称")
    private String deviceName;
    
    @Schema(description = "当前液位值(m)")
    private BigDecimal currentLevel;
    
    @Schema(description = "液位单位")
    private String unit = "m";
}