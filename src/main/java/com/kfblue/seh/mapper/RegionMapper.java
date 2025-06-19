package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfblue.seh.entity.Region;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RegionMapper extends BaseMapper<Region> {

    @Select("SELECT * FROM regions WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY region_code")
    List<Region> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM regions WHERE region_code = #{regionCode} AND deleted = 0")
    Region findByRegionCode(@Param("regionCode") String regionCode);

    @Select("SELECT * FROM regions WHERE region_type = #{regionType} AND deleted = 0 ORDER BY region_level, region_code")
    List<Region> findByRegionType(@Param("regionType") String regionType);

    @Select("SELECT * FROM regions WHERE region_type = #{regionType} AND region_level = #{level} AND deleted = 0 ORDER BY region_code")
    List<Region> findByRegionTypeAndLevel(@Param("regionType") String regionType, @Param("level") Integer level);

    @Select("SELECT * FROM regions WHERE deleted = 0 ORDER BY region_level, region_code")
    List<Region> findAll();

    /**
     * 根据ID查询区域
     *
     * @param id 区域ID
     * @return 区域信息
     */
    @Select("SELECT * FROM regions WHERE id = #{id} AND deleted = 0")
    Region findById(@Param("id") Long id);

    /**
     * 根据层级查询区域
     *
     * @param level 区域层级
     * @return 区域列表
     */
    @Select("SELECT * FROM regions WHERE region_level = #{level} AND deleted = 0 ORDER BY region_code")
    List<Region> findByLevel(@Param("level") Integer level);


    @Insert("INSERT INTO regions (region_code, region_name, parent_id, region_level, region_path, area_size, region_type, status, remark, created_by, created_at, updated_by, updated_at) " +
            "VALUES (#{regionCode}, #{regionName}, #{parentId}, #{regionLevel}, #{regionPath}, #{areaSize}, #{regionType}, #{status}, #{remark}, #{createdBy}, #{createdAt}, #{updatedBy}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Region region);

    @Update("UPDATE regions SET region_code = #{regionCode}, region_name = #{regionName}, parent_id = #{parentId}, " +
            "region_level = #{regionLevel}, region_path = #{regionPath}, area_size = #{areaSize}, region_type = #{regionType}, " +
            "status = #{status}, remark = #{remark}, updated_by = #{updatedBy}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND deleted = 0")
    int update(Region region);

    @Update("UPDATE regions SET deleted = 1, updated_by = #{updatedBy}, updated_at = #{updatedAt} WHERE id = #{id}")
    int deleteById(@Param("id") Long id, @Param("updatedBy") String updatedBy, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    @Update("UPDATE regions SET status = #{status}, updated_by = #{updatedBy}, updated_at = #{updatedAt} WHERE id = #{id} AND deleted = 0")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") String updatedBy, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    @Select("SELECT COUNT(*) FROM regions WHERE parent_id = #{parentId} AND deleted = 0")
    int countByParentId(@Param("parentId") Long parentId);

    @Select("SELECT COUNT(*) FROM devices WHERE region_id = #{regionId} AND deleted = 0")
    int countDevicesByRegionId(@Param("regionId") Long regionId);

    @Select("SELECT * FROM regions WHERE region_path LIKE CONCAT(#{regionPath}, '%') AND deleted = 0")
    List<Region> findByRegionPathPrefix(@Param("regionPath") String regionPath);

    @Update("UPDATE regions SET deleted = 1, updated_by = #{updatedBy}, updated_at = #{updatedAt} WHERE id IN (${ids})")
    int batchDelete(@Param("ids") String ids, @Param("updatedBy") String updatedBy, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    @Select("SELECT * FROM regions WHERE parent_id IS NULL AND deleted = 0 ORDER BY region_code")
    List<Region> findRootRegions();

    @Select("SELECT MAX(region_level) FROM regions WHERE deleted = 0")
    Integer getMaxLevel();

    @Select("SELECT COUNT(*) FROM regions WHERE status = 1 AND deleted = 0")
    int countActiveRegions();

    @Select("SELECT COUNT(*) FROM regions WHERE deleted = 0")
    int countTotalRegions();
}