package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "用量分布")
public class DistributionVO {

    @Schema(description = "区域")
    private String region;

    private Long id;

    @Schema(description = "用量")
    private BigDecimal value;
}
