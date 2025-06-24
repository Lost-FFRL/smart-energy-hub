package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简化流量数据VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "简化流量数据点")
public class SimpleFlowDataVO {
    
    @Schema(description = "时间")
    private String time;
    
    @Schema(description = "流量值")
    private BigDecimal value;
    
    @Schema(description = "流量单位")
    private String unit = "m³/h";
}