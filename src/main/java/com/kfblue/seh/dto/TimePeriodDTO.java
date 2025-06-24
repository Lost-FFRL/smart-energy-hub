package com.kfblue.seh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalTime;

/**
 * 时段配置DTO
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@Schema(description = "时段配置")
public class TimePeriodDTO {

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    @Schema(description = "动作类型(on:开启,off:关闭)")
    @NotBlank(message = "动作类型不能为空")
    @Pattern(regexp = "^(on|off)$", message = "动作类型只能是on或off")
    private String action;

    @Schema(description = "时段描述")
    private String description;

}