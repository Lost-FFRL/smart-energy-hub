package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 开关灯控制记录实体类
 * 
 * @author system
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lighting_control_records")
public class LightingControlRecord extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 控制时间
     */
    private LocalDateTime controlTime;

    /**
     * 控制动作(on:开启,off:关闭,brightness:调光)
     */
    private String controlAction;

    /**
     * 控制前状态(0:关闭,1:开启)
     */
    private Integer beforeStatus;

    /**
     * 控制后状态(0:关闭,1:开启)
     */
    private Integer afterStatus;

    /**
     * 控制前亮度(0-100)
     */
    private Integer beforeBrightness;

    /**
     * 控制后亮度(0-100)
     */
    private Integer afterBrightness;

    /**
     * 控制来源(manual:手动,auto:自动,smart:智能控制)
     */
    private String controlSource;

    /**
     * 智能控制ID(如果是智能控制触发)
     */
    private Long smartControlId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 控制结果(0:失败,1:成功)
     */
    private Integer controlResult;

    /**
     * 错误信息
     */
    private String errorMessage;

    // ========== 便捷方法 ==========
    
    /**
     * 是否控制成功
     */
    public boolean isSuccess() {
        return controlResult != null && controlResult == 1;
    }

    /**
     * 是否手动控制
     */
    public boolean isManualControl() {
        return "manual".equals(controlSource);
    }

    /**
     * 是否自动控制
     */
    public boolean isAutoControl() {
        return "auto".equals(controlSource);
    }

    /**
     * 是否智能控制
     */
    public boolean isSmartControl() {
        return "smart".equals(controlSource);
    }

    /**
     * 获取控制动作描述
     */
    public String getControlActionDescription() {
        switch (controlAction) {
            case "on":
                return "开启";
            case "off":
                return "关闭";
            case "brightness":
                return "调光";
            default:
                return "未知动作";
        }
    }

    /**
     * 获取控制来源描述
     */
    public String getControlSourceDescription() {
        switch (controlSource) {
            case "manual":
                return "手动控制";
            case "auto":
                return "自动控制";
            case "smart":
                return "智能控制";
            default:
                return "未知来源";
        }
    }

    /**
     * 获取控制结果描述
     */
    public String getControlResultDescription() {
        return isSuccess() ? "成功" : "失败";
    }
}