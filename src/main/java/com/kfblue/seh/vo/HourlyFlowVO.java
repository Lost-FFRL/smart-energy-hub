package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小时流量数据VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "小时流量数据")
public class HourlyFlowVO {
    
    @Schema(description = "小时")
    private String hour;
    
    @Schema(description = "平均流量(m³/h)")
    private BigDecimal avgFlow;
    
    @Schema(description = "最大流量(m³/h)")
    private BigDecimal maxFlow;
    
    @Schema(description = "最小流量(m³/h)")
    private BigDecimal minFlow;
    
    @Schema(description = "累计流量(m³)")
    private BigDecimal totalFlow;
}