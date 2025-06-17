package com.kfblue.seh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfblue.seh.entity.DeviceDailyStatistic;
import com.kfblue.seh.mapper.DeviceDailyStatisticMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 设备日统计服务实现类
 */
@Service
public class DeviceDailyStatisticService extends ServiceImpl<DeviceDailyStatisticMapper, DeviceDailyStatistic> {
    // TODO: 实现业务方法
}