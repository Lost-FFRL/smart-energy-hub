package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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
     * 区域层级(1:一级,2:二级,3:三级等)
     */
    private Integer regionLevel;

    /**
     * 区域路径,用/分隔
     */
    private String regionPath;

    /**
     * 区域面积(平方米)
     */
    private BigDecimal areaSize;

    /**
     * 区域类型(office:办公,workshop:车间,dormitory:宿舍等)
     */
    private String regionType;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 逻辑删除标记(0:正常,1:删除)
     */
    @TableLogic
    private Integer deleted;
}