# 照明设备状态模拟定时任务

## 功能概述

本项目新增了照明设备状态模拟定时任务，用于模拟真实环境中照明设备的状态变化，包括在线状态、工作状态和告警状态的动态变化。

## 核心特性

### 1. 定时执行
- **执行频率**: 每10分钟自动执行一次
- **Cron表达式**: `0 */10 * * * ?`
- **自动启动**: 应用启动后自动开始执行

### 2. 智能状态控制
- **离线率控制**: 确保设备离线率不超过10%
- **告警率控制**: 确保设备告警率不超过10%
- **动态调整**: 根据当前状态智能调整设备状态变化策略

### 3. 状态变化模拟
- **在线状态**: 20%概率发生变化，优先保持在线
- **告警状态**: 15%概率发生变化，优先保持正常
- **工作状态**: 30%概率发生变化（仅限在线设备）

## 文件结构

```
src/main/java/com/kfblue/seh/
├── scheduler/
│   └── LightingDeviceStatusScheduler.java    # 定时任务主类
├── controller/
│   └── LightingDeviceController.java         # 照明设备控制器
└── service/
    └── LightingDeviceService.java            # 照明设备服务类
```

## 主要类说明

### LightingDeviceStatusScheduler

照明设备状态模拟定时任务的核心类，负责：

- 定时执行设备状态模拟
- 智能控制离线率和告警率
- 记录状态变化日志
- 提供手动执行接口

**关键方法**:
- `simulateDeviceStatusChanges()`: 定时任务入口方法
- `simulateDeviceStatus()`: 设备状态模拟核心逻辑
- `calculateCurrentStats()`: 计算当前设备状态统计

### LightingDeviceController

照明设备的REST API控制器，提供：

- 设备查询接口
- 设备统计接口
- 设备状态更新接口
- **手动触发模拟接口**: `POST /api/lighting-devices/simulate-status`

### LightingDeviceService

照明设备业务逻辑服务类，提供：

- 设备CRUD操作
- 设备统计计算
- 设备状态更新

## API接口

### 手动触发设备状态模拟

```http
POST /api/lighting-devices/simulate-status
```

**响应示例**:
```json
{
  "success": true,
  "message": "设备状态模拟执行成功"
}
```

### 获取设备统计信息

```http
GET /api/lighting-devices/statistics
```

**响应示例**:
```json
{
  "total": 100,
  "online": 92,
  "offline": 8,
  "working": 85,
  "alarm": 7,
  "onlineRate": 0.92,
  "offlineRate": 0.08,
  "alarmRate": 0.07
}
```

## 配置说明

### 定时任务配置

定时任务通过Spring的`@Scheduled`注解配置：

```java
@Scheduled(cron = "0 */10 * * * ?")
public void simulateDeviceStatusChanges() {
    // 定时任务逻辑
}
```

### 状态控制参数

```java
// 最大离线率 10%
private static final double MAX_OFFLINE_RATE = 0.10;
// 最大告警率 10%
private static final double MAX_ALARM_RATE = 0.10;
```

## 日志监控

定时任务执行时会输出详细的日志信息：

```
2025-01-27 10:00:00 INFO  - 开始执行照明设备状态模拟任务
2025-01-27 10:00:00 INFO  - 找到 100 个照明设备，开始模拟状态变化
2025-01-27 10:00:00 INFO  - 当前设备状态 - 总数: 100, 离线: 8, 告警: 7, 离线率: 8.00%, 告警率: 7.00%
2025-01-27 10:00:01 INFO  - 设备状态模拟完成，共更新 15 个设备状态
2025-01-27 10:00:01 INFO  - 最终设备状态 - 总数: 100, 离线: 9, 告警: 6, 离线率: 9.00%, 告警率: 6.00%
2025-01-27 10:00:01 INFO  - 照明设备状态模拟任务执行完成
```

## 测试方法

### 1. 手动触发测试

使用API接口手动触发定时任务：

```bash
curl -X POST http://localhost:8080/api/lighting-devices/simulate-status
```

### 2. 查看统计信息

```bash
curl -X GET http://localhost:8080/api/lighting-devices/statistics
```

### 3. 日志监控

查看应用日志，观察定时任务的执行情况和设备状态变化。

## 注意事项

1. **数据库依赖**: 确保`lighting_device`表存在且有数据
2. **定时任务启用**: 主应用类需要添加`@EnableScheduling`注解
3. **状态约束**: 离线设备无法改变工作状态
4. **性能考虑**: 大量设备时可能需要优化批量更新逻辑
5. **错误处理**: 定时任务执行失败不会影响应用正常运行

## 扩展建议

1. **配置化参数**: 将离线率、告警率等参数配置到配置文件中
2. **分批处理**: 对于大量设备，可以考虑分批处理以提高性能
3. **状态历史**: 记录设备状态变化历史，便于分析
4. **告警通知**: 当离线率或告警率超过阈值时发送通知
5. **可视化监控**: 提供设备状态变化的可视化监控界面