package com.kfblue.seh.vo;

import com.kfblue.seh.dto.TimePeriodDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 照明配置VO
 *
 * @author system
 * @since 2025-01-27
 */
@Data
public class LightingConfigVO {

    @Schema(description = "配置ID")
    private Long id;
    
    @Schema(description = "配置名称")
    private String configName;
    
    @Schema(description = "控制模式(longitude:经纬度,manual:手动,timer:定时)")
    private String controlMode;
    
    @Schema(description = "区域ID")
    private Long regionId;
    
    @Schema(description = "设备类型")
    private String deviceType;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @Schema(description = "时段配置列表")
    private List<TimePeriodDTO> timePeriods;
    
    @Schema(description = "目标设备列表")
    private List<String> targetDevices;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}