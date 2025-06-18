package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用水分析统计VO
 */
@Data
@Schema(description = "用水分析统计")
public class AnalysisStatisticsVO {
    
    @Schema(description = "统计类型")
    private String statisticsType;
    
    @Schema(description = "时间维度")
    private String timeDimension;
    
    @Schema(description = "用量统计")
    private ConsumptionStatVO consumptionStat;
    
    @Schema(description = "效率统计")
    private EfficiencyStatVO efficiencyStat;
    
    @Schema(description = "成本统计")
    private CostStatVO costStat;
    
    @Schema(description = "分类统计")
    private List<CategoryStatVO> categoryStats;
    
    /**
     * 用量统计
     */
    @Data
    @Schema(description = "用量统计")
    public static class ConsumptionStatVO {
        
        @Schema(description = "总用水量")
        private BigDecimal totalConsumption;
        
        @Schema(description = "平均用水量")
        private BigDecimal avgConsumption;
        
        @Schema(description = "峰值用水量")
        private BigDecimal peakConsumption;
        
        @Schema(description = "谷值用水量")
        private BigDecimal valleyConsumption;
        
        @Schema(description = "用水量单位")
        private String unit;
        
        @Schema(description = "环比增长率")
        private BigDecimal momRate;
        
        @Schema(description = "同比增长率")
        private BigDecimal yoyRate;
    }
    
    /**
     * 效率统计
     */
    @Data
    @Schema(description = "效率统计")
    public static class EfficiencyStatVO {
        
        @Schema(description = "用水效率")
        private BigDecimal efficiency;
        
        @Schema(description = "效率单位")
        private String efficiencyUnit;
        
        @Schema(description = "节水量")
        private BigDecimal waterSaved;
        
        @Schema(description = "节水率")
        private BigDecimal waterSavedRate;
        
        @Schema(description = "设备利用率")
        private BigDecimal deviceUtilization;
        
        @Schema(description = "运行时长")
        private BigDecimal runningHours;
    }
    
    /**
     * 成本统计
     */
    @Data
    @Schema(description = "成本统计")
    public static class CostStatVO {
        
        @Schema(description = "总成本")
        private BigDecimal totalCost;
        
        @Schema(description = "单位成本")
        private BigDecimal unitCost;
        
        @Schema(description = "成本单位")
        private String costUnit;
        
        @Schema(description = "运维成本")
        private BigDecimal maintenanceCost;
        
        @Schema(description = "能耗成本")
        private BigDecimal energyCost;
        
        @Schema(description = "成本环比")
        private BigDecimal costMomRate;
        
        @Schema(description = "成本同比")
        private BigDecimal costYoyRate;
    }
    
    /**
     * 分类统计
     */
    @Data
    @Schema(description = "分类统计")
    public static class CategoryStatVO {
        
        @Schema(description = "分类名称")
        private String categoryName;
        
        @Schema(description = "分类值")
        private BigDecimal value;
        
        @Schema(description = "占比")
        private BigDecimal percentage;
        
        @Schema(description = "单位")
        private String unit;
        
        @Schema(description = "环比增长率")
        private BigDecimal momRate;
    }
}