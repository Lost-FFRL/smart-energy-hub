package com.kfblue.seh.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 区域树形结构视图对象
 *
 * @author system
 * @since 2025-01-20
 */
@Data
public class RegionTreeVO {

    /**
     * 区域ID
     */
    private Long id;

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
     * 备注信息
     */
    private String remark;

    /**
     * 子区域列表
     */
    private List<RegionTreeVO> children;

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;

    /**
     * 设备数量
     */
    private Integer deviceCount;

    /**
     * 区域类型显示名称
     */
    private String regionTypeLabel;

    /**
     * 状态显示名称
     */
    private String statusLabel;
}