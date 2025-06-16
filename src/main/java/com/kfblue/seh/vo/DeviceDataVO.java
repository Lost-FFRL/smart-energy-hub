package com.kfblue.seh.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备数据视图对象
 */
@Data
public class DeviceDataVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 网关
     */
    private String XGateway;

    /**
     * 标签名称
     */
    private String XTagName;

    /**
     * 标签值
     */
    private String XValue;

    /**
     * 数据质量
     */
    private Integer XQuality;

    /**
     * 时间戳
     */
    private LocalDateTime XTimeStamp;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 获取数据质量描述
     * @param dataQuality 数据质量值
     * @return 描述文本
     */
    public static String getDataQualityDesc(Integer dataQuality) {
        if (dataQuality == null) {
            return "未知";
        }
        return dataQuality == 1 ? "好值" : "坏值";
    }
    
    /**
     * 格式化时间戳为可读时间
     * @param timestamp 时间戳
     * @return 格式化时间字符串
     */
    public static String formatTimestamp(Long timestamp) {
        if (timestamp == null) {
            return "";
        }
        return java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}