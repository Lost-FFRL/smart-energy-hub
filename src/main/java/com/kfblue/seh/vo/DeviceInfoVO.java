package com.kfblue.seh.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备信息VO
 */
@Data
public class DeviceInfoVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备类型
     */
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
    private String status;
    
    /**
     * 设备状态描述
     */
    private String statusDesc;
    
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
     * 是否在保修期内
     */
    private Boolean inWarranty;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 备注
     */
    private String remark;
}