package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用水分析排行榜VO
 */
@Data
@Schema(description = "分析排行榜")
public class AnalysisRankVO {
    
    @Schema(description = "排名")
    private Integer rank;
    
    @Schema(description = "名称（设备名称或区域名称）")
    private String name;
    
    @Schema(description = "用水量")
    private BigDecimal consumption;
    
    @Schema(description = "用水量单位")
    private String unit;
    
    @Schema(description = "占比")
    private BigDecimal percentage;
    
    @Schema(description = "环比增长率")
    private BigDecimal momRate;
    
    @Schema(description = "同比增长率")
    private BigDecimal yoyRate;
    
    @Schema(description = "设备ID（当排行类型为设备时）")
    private Long deviceId;
    
    @Schema(description = "区域ID（当排行类型为区域时）")
    private Long regionId;
    
    @Schema(description = "设备类型")
    private String deviceType;
    
    @Schema(description = "状态（在线/离线）")
    private String status;
    
    @Schema(description = "最后更新时间")
    private String lastUpdateTime;
}