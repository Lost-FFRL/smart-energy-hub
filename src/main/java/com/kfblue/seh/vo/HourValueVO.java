package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "小时与数值VO")
public class HourValueVO {

    @Schema(description = "小时(0-23)")
    private Integer hour;

    @Schema(description = "数值")
    private Double value;
}