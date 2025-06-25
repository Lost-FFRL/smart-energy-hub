package com.kfblue.seh.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果VO
 * 
 * @param <T> 数据类型
 * @author system
 * @since 2025-01-27
 */
@Data
public class PageVO<T> {
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return current != null && current > 1;
    }
    
    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return current != null && pages != null && current < pages;
    }
}