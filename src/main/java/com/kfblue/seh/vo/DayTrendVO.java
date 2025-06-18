package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "日量")
public class DayTrendVO {

    @Schema(description = "7天用量列表")
    private List<DayValueVO> trends;

    @Schema(description = "当日用量")
    private BigDecimal current;

    @Schema(description = "环比是与上一个相邻时间段的比较，环比增长率 =(当日 - 前一日) / 前一日 × 100%")
    private BigDecimal momRate;

    @Schema(description = "同比是与去年同期的比较，同比增长率 =(今年当日 - 去年当日) / 去年当日 × 100%")
    private BigDecimal yoyRate;

    @Schema(description = "单位")
    private String unit;


}