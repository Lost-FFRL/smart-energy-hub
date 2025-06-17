package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "用量排行榜")
public class RankVO {

    @Schema(description = "区域")
    private String region;

    private Long id;

    @Schema(description = "用量")
    private Double current;

}