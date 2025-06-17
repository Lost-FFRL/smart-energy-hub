package com.kfblue.seh.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "能耗设备类型统计VO")
public class DeviceTypeStatVO {
    @Schema(description = "设备类型(water:水表,electric:电表,gas:气表,heat:热表)")
    private String deviceType;

    @Schema(description = "设备数量")
    private Integer count;
}