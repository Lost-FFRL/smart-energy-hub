package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 照明配置VO
 *
 * @author system
 * @since 2025-01-27
 */
@Data
public class LightDeviceSimpleVO {

    @Schema(description = "配置ID")
    private Long id;
    @Schema(description = "设备编码")
    private String deviceCode;
    @Schema(description = "设备名称")
    private String deviceName;
    @Schema(description = "设备类型")
    private String deviceType;
    @Schema(description = "在线状态(0:离线,1:在线)")
    private Integer onlineStatus;
    @Schema(description = "工作状态(0:关闭,1:开启)")
    private Integer workStatus;
    @Schema(description = "告警状态(0:正常,1:告警)")
    private Integer alarmStatus;

}