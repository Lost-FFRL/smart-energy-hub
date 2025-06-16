package com.kfblue.seh.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Demo实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo")
public class Demo extends BaseEntity {
    
    /**
     * 名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 状态(0:禁用,1:启用)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;
}