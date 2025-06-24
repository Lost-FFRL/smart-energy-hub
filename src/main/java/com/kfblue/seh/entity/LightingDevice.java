package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * 照明设备实体类
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lighting_devices")
public class LightingDevice extends BaseEntity {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 所属区域ID
     */
    private Long regionId;

    /**
     * 设备类型(single_light:单灯,light_box:灯箱,LED,荧光灯,白炽灯,智能灯)
     */
    private String deviceType;

    /**
     * 在线状态(0:离线,1:在线)
     */
    private Integer onlineStatus;

    /**
     * 工作状态(0:关闭,1:开启)
     */
    private Integer workStatus;

    /**
     * 告警状态(0:正常,1:告警)
     */
    private Integer alarmStatus;
}