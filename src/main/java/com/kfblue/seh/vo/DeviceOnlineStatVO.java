package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DeviceOnlineStatVO {
    @Schema(description = "在线设备量")
    private Integer onlineCnt;

    @Schema(description = "离线设备量")
    private Integer offlineCnt;

    @Schema(description = "设备总数")
    private Integer totalCnt;

    @Schema(description = "在线率，百分比字符串如99.9%")
    private BigDecimal onlineRate;
}