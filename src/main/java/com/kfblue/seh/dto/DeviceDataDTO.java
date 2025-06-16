package com.kfblue.seh.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 设备数据传输对象
 */
@Data
public class DeviceDataDTO {
    
    @NotBlank(message = "网关不能为空")
    private String XGateway;

    @NotBlank(message = "标签名称不能为空")
    private String XTagName;

    @NotBlank(message = "标签值不能为空")
    private String XValue;

    private Integer XQuality;

    @NotNull(message = "时间戳不能为空")
    private LocalDateTime XTimeStamp;
    
    /**
     * 备注信息
     */
    private String remark;
}