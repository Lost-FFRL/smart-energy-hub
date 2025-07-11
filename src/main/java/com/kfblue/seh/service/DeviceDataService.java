package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceData;
import com.kfblue.seh.mapper.DeviceDataMapper;
import com.kfblue.seh.vo.DeviceDataVO;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 设备数据服务实现类
 */
@Service
public class DeviceDataService extends ServiceImpl<DeviceDataMapper, DeviceData> {
    // TODO: 实现业务方法
}