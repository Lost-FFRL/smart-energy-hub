package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "小时用量对比")
public class HourCompareVO {

    @Schema(description = "昨日24小时用量列表")
    private List<HourValueVO> yesterdays;

    @Schema(description = "今日24小时用量列表")
    private List<HourValueVO> todays;

}