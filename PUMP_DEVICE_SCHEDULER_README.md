# 水泵设备数据生成定时任务

## 概述

本文档描述了水泵设备数据生成定时任务的实现，该任务每小时自动生成水泵设备的运行数据以及相关的流量监控和液位监控流水表数据。

## 功能特性

### 核心功能
- **定时执行**: 每小时的第0分钟自动执行（cron表达式: `0 0 * * * ?`）
- **设备状态模拟**: 自动更新水泵设备的在线状态、工作状态、报警状态等
- **流量数据生成**: 为每个设备生成流量监控数据（flow_monitor表）
- **液位数据生成**: 为每个设备生成液位监控数据（level_monitor表）
- **批量处理**: 支持批量更新设备状态和保存监控数据

### 数据模拟规则

#### 设备状态模拟
- **在线率**: 95%（随机模拟5%的设备离线）
- **工作率**: 在线设备中80%处于工作状态
- **报警率**: 5%的设备处于报警状态
- **运行参数**: 模拟频率、电流、电压、压力、功率等实时数据

#### 流量监控数据
- **瞬时流量**: 根据设备额定流量的70%-110%范围内随机生成
- **累计流量**: 基于上次记录累加瞬时流量
- **流速**: 根据管径自动计算
- **温度**: 15-25°C范围内随机
- **压力**: 0.2-0.6MPa范围内随机
- **数据质量**: 95%的数据质量为良好

#### 液位监控数据
- **液位变化**: 工作状态下液位下降（抽水），停止状态下可能上升或保持
- **容器参数**: 模拟5米高、1000立方米容量的储水设施
- **报警阈值**: 高位报警90%，低位报警10%
- **温度**: 10-30°C范围内随机
- **密度**: 0.98-1.02 g/cm³范围内随机

## 文件结构

```
src/main/java/com/kfblue/seh/
├── scheduler/
│   └── PumpDeviceDataScheduler.java     # 水泵设备数据定时任务
├── controller/
│   └── PumpDeviceController.java        # 水泵设备REST API控制器
├── service/
│   ├── PumpDeviceService.java           # 水泵设备服务类
│   ├── FlowMonitorService.java          # 流量监控服务类
│   └── LevelMonitorService.java         # 液位监控服务类
└── entity/
    ├── PumpDevice.java                  # 水泵设备实体
    ├── FlowMonitor.java                 # 流量监控实体
    └── LevelMonitor.java                # 液位监控实体
```

## 主要类说明

### PumpDeviceDataScheduler

水泵设备数据定时任务调度器，负责：
- 定时执行数据生成任务
- 更新设备状态
- 生成流量和液位监控数据
- 批量保存数据

**关键方法**:
- `generatePumpDeviceData()`: 主要的定时任务方法
- `manualGenerateData()`: 手动触发数据生成（用于测试）
- `updateDeviceStatus()`: 更新设备状态
- `generateFlowMonitorData()`: 生成流量监控数据
- `generateLevelMonitorData()`: 生成液位监控数据

### PumpDeviceController

REST API控制器，提供：
- 设备管理接口
- 手动触发数据生成接口
- 设备状态查询和更新接口

## API 接口

### 手动触发数据生成
```http
POST /api/pump-devices/manual-generate-data
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "水泵设备数据生成任务已触发"
}
```

### 获取设备统计信息
```http
GET /api/pump-devices/statistics?regionId=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 10,
    "onlineCount": 9,
    "offlineCount": 1,
    "workingCount": 7,
    "alarmCount": 1,
    "onlineRate": 90.0,
    "offlineRate": 10.0,
    "alarmRate": 10.0
  }
}
```

### 分页查询设备
```http
GET /api/pump-devices?page=1&size=10&regionId=1&deviceName=pump
```

## 配置说明

### 定时任务配置

定时任务使用Spring的`@Scheduled`注解，cron表达式为`0 0 * * * ?`，表示每小时的第0分钟执行。

如需修改执行频率，可以调整cron表达式：
- 每30分钟执行: `0 0,30 * * * ?`
- 每15分钟执行: `0 0,15,30,45 * * * ?`
- 每天凌晨2点执行: `0 0 2 * * ?`

### 应用配置

确保主应用类已启用定时任务：
```java
@SpringBootApplication
@EnableScheduling  // 启用定时任务
public class SmartEnergyHubApplication {
    // ...
}
```

## 日志监控

定时任务会输出详细的日志信息：

```
2025-01-27 10:00:00.123 INFO  [scheduling-1] c.k.s.s.PumpDeviceDataScheduler : 开始生成水泵设备数据和流水表数据...
2025-01-27 10:00:01.456 INFO  [scheduling-1] c.k.s.s.PumpDeviceDataScheduler : 水泵设备数据生成完成 - 设备数量: 10, 流量数据: 成功, 液位数据: 成功
```

## 测试方法

### 1. 手动触发测试

使用API接口手动触发数据生成：
```bash
curl -X POST http://localhost:8080/api/pump-devices/manual-generate-data
```

### 2. 查看生成的数据

查询流量监控数据：
```sql
SELECT * FROM flow_monitor ORDER BY monitor_time DESC LIMIT 10;
```

查询液位监控数据：
```sql
SELECT * FROM level_monitor ORDER BY monitor_time DESC LIMIT 10;
```

查询设备状态：
```sql
SELECT device_code, device_name, online_status, work_status, alarm_status, last_online_time 
FROM pump_device ORDER BY updated_at DESC;
```

### 3. 监控日志

观察应用日志，确认定时任务正常执行：
```bash
tail -f logs/application.log | grep PumpDeviceDataScheduler
```

## 注意事项

1. **数据库性能**: 批量插入大量数据时，注意数据库性能影响
2. **事务管理**: 确保数据一致性，避免部分数据保存失败
3. **异常处理**: 定时任务中的异常不会中断后续执行
4. **数据清理**: 定期清理历史监控数据，避免数据表过大
5. **监控告警**: 建议配置监控告警，及时发现定时任务执行异常

## 扩展建议

1. **数据压缩**: 对历史数据进行压缩存储
2. **分区表**: 使用时间分区表提高查询性能
3. **数据分析**: 基于生成的数据进行趋势分析和预测
4. **告警规则**: 根据监控数据配置自动告警规则
5. **数据导出**: 提供数据导出功能，支持Excel、CSV等格式

## 故障排查

### 常见问题

1. **定时任务不执行**
   - 检查`@EnableScheduling`注解是否添加
   - 确认应用是否正常启动
   - 查看日志是否有异常信息

2. **数据保存失败**
   - 检查数据库连接是否正常
   - 确认表结构是否正确
   - 查看是否有字段约束冲突

3. **性能问题**
   - 调整批量保存的数据量
   - 优化数据库索引
   - 考虑异步处理

### 调试模式

开启调试日志：
```yaml
logging:
  level:
    com.kfblue.seh.scheduler: DEBUG
```

这将输出更详细的执行信息，便于问题排查。