package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 液位监控数据VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "液位监控数据")
public class LevelMonitorVO {
    
    @Schema(description = "当前液位(m)")
    private BigDecimal currentLevel;
    
    @Schema(description = "液位百分比(%)")
    private BigDecimal levelPercentage;
    
    @Schema(description = "当前容量(m³)")
    private BigDecimal currentCapacity;
    
    @Schema(description = "总容量(m³)")
    private BigDecimal totalCapacity;
    
    @Schema(description = "液位单位")
    private String levelUnit = "m";
    
    @Schema(description = "报警状态(0:正常,1:高液位,2:低液位,3:超高液位,4:超低液位)")
    private Integer alarmStatus;
    
    @Schema(description = "报警信息")
    private String alarmMessage;
    
    @Schema(description = "液位趋势数据")
    private List<LevelTrendVO> levelTrend;
}