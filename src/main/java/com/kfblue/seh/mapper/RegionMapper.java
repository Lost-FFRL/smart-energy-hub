package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 区域Mapper接口
 * 
 * @author system
 * @since 2025-06-17
 */
@Mapper
public interface RegionMapper extends BaseMapper<Region> {

    /**
     * 根据父级ID查询子区域列表
     * 
     * @param parentId 父级区域ID
     * @return 子区域列表
     */
    @Select("SELECT * FROM regions WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY region_code")
    List<Region> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据区域编码查询区域
     * 
     * @param regionCode 区域编码
     * @return 区域信息
     */
    @Select("SELECT * FROM regions WHERE region_code = #{regionCode} AND deleted = 0")
    Region selectByRegionCode(@Param("regionCode") String regionCode);

    /**
     * 根据区域类型查询区域列表
     * 
     * @param regionType 区域类型
     * @return 区域列表
     */
    @Select("SELECT * FROM regions WHERE region_type = #{regionType} AND deleted = 0 ORDER BY region_code")
    List<Region> selectByRegionType(@Param("regionType") String regionType);

    /**
     * 查询区域树结构
     * 
     * @return 区域树列表
     */
    @Select("SELECT * FROM regions WHERE deleted = 0 ORDER BY region_level, region_code")
    List<Region> selectRegionTree();
}