package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kfblue.seh.entity.DeviceData;
import com.kfblue.seh.vo.DeviceDataVO;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 设备数据服务接口
 */
public interface DeviceDataService extends IService<DeviceData> {
    
    /**
     * 分页查询设备数据
     * @param page 分页参数
     * @param XGateway 网关
     * @param XTagName 标签名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<DeviceDataVO> getDeviceDataPage(Page<DeviceData> page,
                                          String XGateway,
                                          String XTagName,
                                          String startTime,
                                          String endTime);

    /**
     * 根据网关和标签名称获取最新数据
     * @param XGateway 网关
     * @param XTagName 标签名称
     * @return 最新设备数据
     */
    DeviceDataVO getLatestByGatewayAndTag(String XGateway, String XTagName);
    
    /**
     * 批量保存设备数据
     * @param dataList 设备数据列表
     * @return 保存成功的条数
     */
    int batchSave(List<DeviceData> dataList);
    
    /**
     * 单条保存设备数据
     * @param deviceData 设备数据
     * @return 是否保存成功
     */
    boolean saveDeviceData(DeviceData deviceData);
    
    /**
     * 根据时间范围删除历史数据
     * @param beforeTime 删除此时间之前的数据
     * @return 删除的记录数
     */
    int deleteByTimeRange(LocalDateTime beforeTime);
    
    /**
     * 根据网关获取最新数据（每个标签一条）
     * @param XGateway 网关
     * @return 最新数据列表
     */
    List<DeviceData> getLatestDataByGateway(String XGateway);
    
    /**
     * 根据标签名称获取最新数据（每个网关一条）
     * @param XTagName 标签名称
     * @return 最新数据列表
     */
    List<DeviceData> getLatestDataByTag(String XTagName);
}