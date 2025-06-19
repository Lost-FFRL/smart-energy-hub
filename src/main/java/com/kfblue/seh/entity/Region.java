package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区域实体类
 *
 * @author system
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("regions")
public class Region extends BaseEntity {

    /**
     * 区域编码
     */
    private String regionCode;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 父级区域ID
     */
    private Long parentId;

    /**
     * 区域层级
     */
    private Integer regionLevel;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 区域面积
     */
    private Double areaSize;

    /**
     * 区域类型
     */
    private String regionType;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}