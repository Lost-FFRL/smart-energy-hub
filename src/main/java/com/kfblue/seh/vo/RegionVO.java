package com.kfblue.seh.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 区域实体类
 *
 * @author system
 * @since 2025-06-17
 */
@Data
public class RegionVO {

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

}