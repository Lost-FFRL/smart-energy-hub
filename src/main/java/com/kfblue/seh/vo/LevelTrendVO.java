package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 液位趋势数据VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "液位趋势数据")
public class LevelTrendVO {
    
    @Schema(description = "时间")
    private String time;
    
    @Schema(description = "液位(m)")
    private BigDecimal level;
    
    @Schema(description = "液位百分比(%)")
    private BigDecimal percentage;
    
    @Schema(description = "容量(m³)")
    private BigDecimal capacity;
}