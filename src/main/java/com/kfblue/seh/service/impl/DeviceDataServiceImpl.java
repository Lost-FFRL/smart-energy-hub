package com.kfblue.seh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceData;
import com.kfblue.seh.mapper.DeviceDataMapper;
import com.kfblue.seh.service.DeviceDataService;
import com.kfblue.seh.vo.DeviceDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备数据服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData> implements DeviceDataService {
    
    private final DeviceDataMapper deviceDataMapper;
    
    @Override
    public IPage<DeviceDataVO> getDeviceDataPage(Page<DeviceData> page, String XGateway, 
                                                String XTagName, String startTime, String endTime) {
        IPage<DeviceData> dataPage = deviceDataMapper.selectPageWithConditions(page, XGateway, XTagName, startTime, endTime);
        
        // 转换为VO对象
        IPage<DeviceDataVO> voPage = dataPage.convert(data -> {
            DeviceDataVO vo = new DeviceDataVO();
            BeanUtils.copyProperties(data, vo);
            return vo;
        });
        
        return voPage;
    }
    
    @Override
    public DeviceDataVO getLatestByGatewayAndTag(String XGateway, String XTagName) {
        DeviceData data = deviceDataMapper.selectLatestByGatewayAndTag(XGateway, XTagName);
        if (data == null) {
            return null;
        }
        
        DeviceDataVO vo = new DeviceDataVO();
        BeanUtils.copyProperties(data, vo);
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSave(List<DeviceData> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        dataList.forEach(data -> {
            if (data.getCreatedAt() == null) {
                data.setCreatedAt(now);
            }
            if (data.getUpdatedAt() == null) {
                data.setUpdatedAt(now);
            }
            if (data.getDeleted() == null) {
                data.setDeleted(0);
            }
        });
        
        try {
            int result = deviceDataMapper.batchInsert(dataList);
            log.info("批量保存设备数据成功，共{}条", result);
            return result;
        } catch (Exception e) {
            log.error("批量保存设备数据失败", e);
            throw e;
        }
    }
    
    @Override
    public boolean saveDeviceData(DeviceData deviceData) {
        if (deviceData == null) {
            return false;
        }
        
        try {
            boolean result = this.save(deviceData);
            if (result) {
                log.debug("保存设备数据成功：网关={}, 标签={}, 值={}", 
                         deviceData.getXGateway(), deviceData.getXTagName(), deviceData.getXValue());
            }
            return result;
        } catch (Exception e) {
            log.error("保存设备数据失败：网关={}, 标签={}", 
                     deviceData.getXGateway(), deviceData.getXTagName(), e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByTimeRange(LocalDateTime beforeTime) {
        try {
            int result = deviceDataMapper.deleteByTimeRange(beforeTime);
            log.info("删除历史数据成功，共删除{}条记录，删除时间之前：{}", result, beforeTime);
            return result;
        } catch (Exception e) {
            log.error("删除历史数据失败，删除时间之前：{}", beforeTime, e);
            throw e;
        }
    }
    
    @Override
    public List<DeviceData> getLatestDataByGateway(String XGateway) {
        LambdaQueryWrapper<DeviceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceData::getXGateway, XGateway)
               .orderByDesc(DeviceData::getXTimeStamp)
               .orderByDesc(DeviceData::getId);
        
        List<DeviceData> allData = this.list(wrapper);
        
        // 按标签名称分组，每个标签只保留最新的一条数据
        return allData.stream()
                .collect(Collectors.groupingBy(DeviceData::getXTagName))
                .values()
                .stream()
                .map(list -> list.get(0)) // 每组取第一条（最新的）
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceData> getLatestDataByTag(String XTagName) {
        LambdaQueryWrapper<DeviceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceData::getXTagName, XTagName)
               .orderByDesc(DeviceData::getXTimeStamp)
               .orderByDesc(DeviceData::getId);
        
        List<DeviceData> allData = this.list(wrapper);
        
        // 按网关分组，每个网关的每个标签只保留最新的一条数据
        return allData.stream()
                .collect(Collectors.groupingBy(DeviceData::getXGateway))
                .values()
                .stream()
                .map(list -> list.get(0)) // 每组取第一条（最新的）
                .collect(Collectors.toList());
    }
}