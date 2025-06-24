package com.kfblue.seh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 照明配置DTO
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@Schema(description = "照明配置数据传输对象")
public class LightingConfigDTO {

    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "控制模式(longitude:经纬度,manual:手动,timer:定时)")
    @NotBlank(message = "控制模式不能为空")
    private String controlMode;

    @Schema(description = "所属区域ID(null表示全局配置)")
    private Long regionId;

    @Schema(description = "设备类型(null表示所有设备类型)")
    private String deviceType;

    @Schema(description = "是否启用(0:禁用,1:启用)")
    @NotNull(message = "启用状态不能为空")
    private Integer isEnabled;

    @Schema(description = "时段配置列表")
    @Valid
    private List<TimePeriodDTO> timePeriods;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "目标设备列表")
    private List<String> targetDevices;

    @Schema(description = "备注信息")
    private String remark;
}