package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "日用量对比")
public class DayCompareVO {

    @Schema(description = "当月")
    private List<DayValueVO> currents;

    @Schema(description = "上个月")
    private List<DayValueVO> lasts;

}