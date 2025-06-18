package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用水分析趋势VO
 */
@Data
@Schema(description = "用量分析趋势")
public class AnalysisTrendVO {

    @Schema(description = "今年趋势")
    private List<MonthValueVO> currentYearTrend;
    @Schema(description = "去年趋势")
    private List<MonthValueVO> lastYearTrend;

}