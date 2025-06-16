package com.kfblue.seh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfblue.seh.entity.DeviceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备数据Mapper接口
 */
@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData> {
    
    /**
     * 分页查询设备数据
     * @param XGateway 网关
     * @param XTagName 标签名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 设备数据列表
     */
    IPage<DeviceData> selectPageWithConditions(IPage<DeviceData> page,
                                               @Param("XGateway") String XGateway,
                                               @Param("XTagName") String XTagName,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);

    /**
     * 根据网关和标签名称获取最新数据
     * @param XGateway 网关
     * @param XTagName 标签名称
     * @return 最新设备数据
     */
    DeviceData selectLatestByGatewayAndTag(@Param("XGateway") String XGateway,
                                          @Param("XTagName") String XTagName);
    
    /**
     * 批量插入设备数据
     * @param dataList 设备数据列表
     * @return 插入条数
     */
    int batchInsert(@Param("dataList") List<DeviceData> dataList);
    
    /**
     * 根据时间范围删除历史数据
     * @param beforeTime 删除此时间之前的数据
     * @return 删除的记录数
     */
    int deleteByTimeRange(@Param("beforeTime") LocalDateTime beforeTime);
}