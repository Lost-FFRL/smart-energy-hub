package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用水分析概览VO
 */
@Data
@NoArgsConstructor
@Schema(description = "分析概览")
public class AnalysisDisVO {
    
    @Schema(description = "总用水量")
    private BigDecimal total;

    @Schema(description = "环比增长率")
    private BigDecimal momRate;
    
    @Schema(description = "同比增长率")
    private BigDecimal yoyRate;

    @Schema(description = "用量单位")
    private String unit;
}