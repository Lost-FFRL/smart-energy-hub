package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@Schema(description = "同比分析")
public class YoyVO {

    @Schema(description = "当日总量")
    private BigDecimal todayTotal;
    @Schema(description = "今日当前小时用量")
    private BigDecimal todayHourlyUsage;
    @Schema(description = "昨日总量")
    private BigDecimal yesterdayTotal;
    @Schema(description = "日同期对比")
    private BigDecimal dayRate;

    @Schema(description = "当月总量")
    private BigDecimal currentMonth;
    @Schema(description = "上月总量")
    private BigDecimal lastMonth;
    @Schema(description = "月同期对比")
    private BigDecimal monthRate;

    @Schema(description = "当年总量")
    private BigDecimal currentYear;
    @Schema(description = "去年总量")
    private BigDecimal lastYear;
    @Schema(description = "年同期对比")
    private BigDecimal yearRate;


}