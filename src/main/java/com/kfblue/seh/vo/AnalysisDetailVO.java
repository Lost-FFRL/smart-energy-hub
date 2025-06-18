package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用水分析详情VO
 */
@Data
@Schema(description = "分析详情")
public class AnalysisDetailVO {
    
    @Schema(description = "基础信息")
    private BasicInfoVO basicInfo;
    
    @Schema(description = "用水量详情")
    private ConsumptionDetailVO consumptionDetail;
    
    @Schema(description = "历史趋势")
    private List<HistoryTrendVO> historyTrends;
    
    @Schema(description = "设备列表")
    private List<DeviceDetailVO> devices;
    
    @Schema(description = "时间维度")
    private String timeDimension;
    
    /**
     * 基础信息
     */
    @Data
    @Schema(description = "基础信息")
    public static class BasicInfoVO {
        
        @Schema(description = "区域ID")
        private Long regionId;
        
        @Schema(description = "区域名称")
        private String regionName;
        
        @Schema(description = "设备ID")
        private Long deviceId;
        
        @Schema(description = "设备名称")
        private String deviceName;
        
        @Schema(description = "设备类型")
        private String deviceType;
        
        @Schema(description = "安装位置")
        private String location;
    }
    
    /**
     * 用水量详情
     */
    @Data
    @Schema(description = "用水量详情")
    public static class ConsumptionDetailVO {
        
        @Schema(description = "当前用水量")
        private BigDecimal currentConsumption;
        
        @Schema(description = "上期用水量")
        private BigDecimal previousConsumption;
        
        @Schema(description = "同期用水量")
        private BigDecimal yearOverYearConsumption;
        
        @Schema(description = "环比增长率")
        private BigDecimal momRate;
        
        @Schema(description = "同比增长率")
        private BigDecimal yoyRate;
        
        @Schema(description = "平均日用水量")
        private BigDecimal avgDailyConsumption;
        
        @Schema(description = "峰值用水量")
        private BigDecimal peakConsumption;
        
        @Schema(description = "用水量单位")
        private String unit;
    }
    
    /**
     * 历史趋势
     */
    @Data
    @Schema(description = "历史趋势")
    public static class HistoryTrendVO {
        
        @Schema(description = "时间标签")
        private String timeLabel;
        
        @Schema(description = "用水量")
        private BigDecimal consumption;
        
        @Schema(description = "时间戳")
        private Long timestamp;
    }
    
    /**
     * 设备详情
     */
    @Data
    @Schema(description = "设备详情")
    public static class DeviceDetailVO {
        
        @Schema(description = "设备ID")
        private Long deviceId;
        
        @Schema(description = "设备名称")
        private String deviceName;
        
        @Schema(description = "设备类型")
        private String deviceType;
        
        @Schema(description = "用水量")
        private BigDecimal consumption;
        
        @Schema(description = "状态")
        private String status;
        
        @Schema(description = "最后更新时间")
        private String lastUpdateTime;
    }
}