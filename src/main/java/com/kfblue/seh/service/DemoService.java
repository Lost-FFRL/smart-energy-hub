package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.Demo;
import com.kfblue.seh.mapper.DemoMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Demo服务实现类
 */
@Service
public class DemoService extends ServiceImpl<DemoMapper, Demo> {
    
  
    public IPage<Demo> page(Page<Demo> page, String name, Integer status) {
        LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Demo::getName, name)
               .eq(status != null, Demo::getStatus, status)
               .orderByAsc(Demo::getSortOrder)
               .orderByDesc(Demo::getCreatedAt);
        return this.page(page, wrapper);
    }
    
  
    public Demo getById(Long id) {
        return super.getById(id);
    }
    
  
    public boolean save(Demo demo) {
        return super.save(demo);
    }
    
  
    public boolean update(Demo demo) {
        return super.updateById(demo);
    }
    
  
    public boolean delete(Long id) {
        return super.removeById(id);
    }
}