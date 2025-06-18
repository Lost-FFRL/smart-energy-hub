package com.kfblue.seh.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.Region;
import com.kfblue.seh.mapper.RegionMapper;
import com.kfblue.seh.vo.RegionVO;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 区域服务实现类
 */
@Service
public class RegionService extends ServiceImpl<RegionMapper, Region> {

    public List<RegionVO> selectByLevel(Integer level) {
        level = null == level ? 0 : level;
        return BeanUtil.copyToList(baseMapper.selectByLevel(level), RegionVO.class);
    }

    public Set<Long> selectChildrenIds(Long id) {
        Set<Long> result = new HashSet<>();
        if (null != baseMapper.selectById(id)) {
            result.add(id);
            collectChildrenIds(id, result);
        }
        return result;
    }

    /**
     * 递归收集子节点
     *
     * @param parentId 父节点ID
     * @param result   存储结果的集合
     */
    private void collectChildrenIds(Long parentId, Set<Long> result) {
        List<Region> children = baseMapper.selectList(new QueryWrapper<Region>().eq("parent_id", parentId));
        for (Region child : children) {
            result.add(child.getId());
            collectChildrenIds(child.getId(), result); // 递归查找下一级
        }
    }
}