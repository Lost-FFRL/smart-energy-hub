package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 配电箱
 */
@Data
public class LightVO {
    @Schema(description = "总量")
    private Integer total;
    @Schema(description = "在线数据量")
    private Integer onlineCnt;
    @Schema(description = "离线数据量")
    private Integer offlineCnt;
    @Schema(description = "在线率")
    private BigDecimal onlineRate;
    @Schema(description = "工作数据量")
    private Integer workCnt;
    @Schema(description = "工作率")
    private BigDecimal workRate;
    @Schema(description = "告警数据量")
    private Integer alarmCnt;
    @Schema(description = "告警率")
    private BigDecimal alarmRate;
}