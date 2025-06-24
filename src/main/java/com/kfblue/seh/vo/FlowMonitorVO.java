package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 流量监控数据VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "流量监控数据")
public class FlowMonitorVO {
    
    @Schema(description = "当前瞬时流量(m³/h)")
    private BigDecimal currentFlow;
    
    @Schema(description = "今日累计流量(m³)")
    private BigDecimal todayTotalFlow;
    
    @Schema(description = "本月累计流量(m³)")
    private BigDecimal monthTotalFlow;
    
    @Schema(description = "流量单位")
    private String flowUnit = "m³/h";
    
    @Schema(description = "小时流量趋势")
    private List<HourlyFlowVO> hourlyTrend;
}