package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RankVO {

    @Schema(description = "区域名称")
    private String name;

    private Long id;

    @Schema(description = "用量")
    private BigDecimal current;

}