package com.kfblue.seh.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备信息DTO
 */
@Data
public class DeviceInfoDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;
    
    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;
    
    /**
     * 设备型号
     */
    private String deviceModel;
    
    /**
     * 制造商
     */
    private String manufacturer;
    
    /**
     * 安装日期
     */
    private LocalDate installationDate;
    
    /**
     * 设备状态
     */
    @NotBlank(message = "设备状态不能为空")
    private String status;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 额定功率
     */
    private BigDecimal ratedPower;
    
    /**
     * 额定电压
     */
    private BigDecimal ratedVoltage;
    
    /**
     * 额定电流
     */
    private BigDecimal ratedCurrent;
    
    /**
     * 区域编码
     */
    private String areaCode;
    
    /**
     * 区域名称
     */
    private String areaName;
    
    /**
     * 负责人
     */
    private String responsiblePerson;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 维护周期（天）
     */
    private Integer maintenanceCycle;
    
    /**
     * 保修期至
     */
    private LocalDate warrantyUntil;
    
    /**
     * 备注
     */
    private String remark;
}