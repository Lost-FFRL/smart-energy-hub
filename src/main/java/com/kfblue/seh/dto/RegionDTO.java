package com.kfblue.seh.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 区域数据传输对象
 *
 * @author system
 * @since 2025-01-20
 */
@Data
public class RegionDTO {

    /**
     * 区域编码
     */
    @NotBlank(message = "区域编码不能为空")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "区域编码只能包含大写字母、数字和连字符")
    private String regionCode;

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名称不能为空")
    private String regionName;

    /**
     * 父级区域ID
     */
    private Long parentId;

    /**
     * 区域层级(1:一级,2:二级,3:三级等)
     */
    @NotNull(message = "区域层级不能为空")
    private Integer regionLevel;

    /**
     * 区域面积(平方米)
     */
    private BigDecimal areaSize;

    /**
     * 区域类型(office:办公,workshop:车间,dormitory:宿舍等)
     */
    @NotBlank(message = "区域类型不能为空")
    private String regionType;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status = 1;

    /**
     * 备注信息
     */
    private String remark;
}