package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 水泵设备VO
 * 
 * @author system
 * @since 2025-06-24
 */
@Data
@Schema(description = "水泵设备信息")
public class PumpSimpleVO {

    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

}