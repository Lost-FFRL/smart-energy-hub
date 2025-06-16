package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_data")
public class DeviceData extends BaseEntity {
    
    /**
     * 网关
     */
    @TableField("XGateway")
    private String XGateway;

    /**
     * 标签名称
     */
    @TableField("XTagName")
    private String XTagName;

    /**
     * 值
     */
    @TableField("XValue")
    private String XValue;

    /**
     * 质量
     */
    @TableField("XQuality")
    private String XQuality;

    /**
     * 时间戳
     */
    @TableField("XTimeStamp")
    private String XTimeStamp;
}