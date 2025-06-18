package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class HourlyTrendVO {

    @Schema(description = "昨日24小时用量列表")
    private List<HourValueVO> yesterdays;

    @Schema(description = "今日24小时用量列表")
    private List<HourValueVO> todays;

    @Schema(description = "当前小时用量")
    private BigDecimal currentHour;

    @Schema(description = "环比（当前小时与上一小时数据对比百分比），环比增长率 =（本小时数据 - 上小时数据）/ 上小时数据 × 100%")
    private BigDecimal momRate;

    @Schema(description = "同比（今日当前小时与昨日同小时对比百分比），同比增长率 =（今日数据 - 昨日数据）/ 昨日数据 × 100%")
    private BigDecimal yoyRate;

    @Schema(description = "单位")
    private String unit;
}