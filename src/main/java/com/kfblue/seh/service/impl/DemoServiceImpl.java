package com.kfblue.seh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.Demo;
import com.kfblue.seh.mapper.DemoMapper;
import com.kfblue.seh.service.DemoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Demo服务实现类
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {
    
    @Override
    public IPage<Demo> page(Page<Demo> page, String name, Integer status) {
        LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Demo::getName, name)
               .eq(status != null, Demo::getStatus, status)
               .orderByAsc(Demo::getSortOrder)
               .orderByDesc(Demo::getCreatedAt);
        return this.page(page, wrapper);
    }
    
    @Override
    public Demo getById(Long id) {
        return super.getById(id);
    }
    
    @Override
    public boolean save(Demo demo) {
        return super.save(demo);
    }
    
    @Override
    public boolean update(Demo demo) {
        return super.updateById(demo);
    }
    
    @Override
    public boolean delete(Long id) {
        return super.removeById(id);
    }
}