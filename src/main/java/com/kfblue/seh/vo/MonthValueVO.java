package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "月份与数值VO")
public class MonthValueVO {

    @Schema(description = "年份 1-12")
    private Integer year;

    @Schema(description = "月份 1-12")
    private Integer month;

    @Schema(description = "数值")
    private BigDecimal value;
}