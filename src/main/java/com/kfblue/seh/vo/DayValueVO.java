package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Schema(description = "日期与数值VO")
public class DayValueVO {
    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "数值")
    private Double value;
}