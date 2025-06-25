# 简化测试功能说明

## 概述

本文档介绍了简化版的定时器和模拟数据生成功能，用于快速验证系统的基本功能。

## 功能特性

### 1. 简化定时器 (SimpleSchedulerTest)

- **每分钟执行**: 用于快速验证定时器功能
- **每5分钟执行**: 模拟设备状态变化
- **每10分钟执行**: 模拟数据统计

### 2. 简化测试接口 (SimpleTestController)

- **健康检查**: `/api/simple-test/health`
- **手动触发定时器**: `/api/simple-test/trigger-scheduler`
- **系统状态查询**: `/api/simple-test/status`
- **日志测试**: `/api/simple-test/test-logs`

## 快速开始

### 1. 启动应用

```bash
# 编译项目
mvn clean package -DskipTests

# 启动应用
java -jar target/smart-energy-hub-0.0.1-SNAPSHOT.jar
```

### 2. 验证功能

#### 健康检查
```bash
curl http://localhost:8080/api/simple-test/health
```

#### 手动触发定时器测试
```bash
curl -X POST http://localhost:8080/api/simple-test/trigger-scheduler
```

#### 查看系统状态
```bash
curl http://localhost:8080/api/simple-test/status
```

#### 测试日志输出
```bash
curl -X POST "http://localhost:8080/api/simple-test/test-logs?message=测试消息"
```

## 定时器说明

### 执行频率

| 定时器 | 执行频率 | Cron表达式 | 功能描述 |
|--------|----------|------------|----------|
| 每分钟任务 | 每分钟 | `0 * * * * ?` | 生成简单模拟数据 |
| 每5分钟任务 | 每5分钟 | `0 */5 * * * ?` | 模拟设备状态变化 |
| 每10分钟任务 | 每10分钟 | `0 */10 * * * ?` | 模拟数据统计 |

### 模拟数据类型

#### 简单数据生成
- 温度: 20-30°C
- 湿度: 40-80%
- 功率: 100-600W

#### 设备状态模拟
- 在线率: 90%
- 工作率: 80%（在线设备）
- 告警率: 5%

#### 数据统计模拟
- 设备总数: 50-100台
- 在线率: 85-95%
- 告警率: 0-10%
- 数据生成量: 每台在线设备8-12条数据

## 日志监控

### 查看定时器执行日志

```bash
# 查看应用日志
tail -f logs/application.log

# 或者查看控制台输出
tail -f nohup.out
```

### 日志示例

```
2025-01-27 10:01:00.123 INFO  [scheduling-1] c.k.s.s.SimpleSchedulerTest : [每分钟定时器] 执行时间: 2025-01-27 10:01:00
2025-01-27 10:01:00.125 INFO  [scheduling-1] c.k.s.s.SimpleSchedulerTest : [模拟数据生成] 温度: 25.67°C, 湿度: 62.34%, 功率: 345.12W
2025-01-27 10:05:00.234 INFO  [scheduling-1] c.k.s.s.SimpleSchedulerTest : [每5分钟定时器] 执行时间: 2025-01-27 10:05:00
2025-01-27 10:05:00.236 INFO  [scheduling-1] c.k.s.s.SimpleSchedulerTest : [设备状态模拟] 在线: 是, 工作: 是, 告警: 否
```

## 接口响应示例

### 健康检查响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "UP",
    "timestamp": "2025-01-27 10:00:00",
    "service": "Smart Energy Hub - Simple Test"
  }
}
```

### 系统状态响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "currentTime": "2025-01-27 10:00:00",
    "javaVersion": "17.0.2",
    "osName": "Mac OS X",
    "memory": {
      "total": "512.00 MB",
      "used": "256.00 MB",
      "free": "256.00 MB",
      "usagePercent": "50.00%"
    },
    "scheduler": {
      "enabled": true,
      "tasks": 3,
      "description": "每分钟、每5分钟、每10分钟定时任务"
    }
  }
}
```

## 故障排除

### 常见问题

1. **定时器不执行**
   - 检查应用是否正常启动
   - 查看日志是否有错误信息
   - 确认Spring Boot的定时任务功能已启用

2. **接口访问失败**
   - 确认应用端口8080是否正常监听
   - 检查防火墙设置
   - 验证接口路径是否正确

3. **日志不输出**
   - 检查日志配置
   - 确认日志级别设置
   - 查看日志文件权限

### 调试命令

```bash
# 检查端口占用
lsof -i:8080

# 检查进程状态
ps aux | grep java

# 查看最新日志
tail -n 100 logs/application.log
```

## 扩展说明

这个简化版本专注于验证核心功能：

1. **定时器机制**: 确认Spring的@Scheduled注解正常工作
2. **数据模拟**: 验证随机数据生成逻辑
3. **日志输出**: 确认日志系统正常运行
4. **接口访问**: 验证Web服务和路由配置

如需完整功能，可以参考原有的复杂定时器实现：
- `DeviceDataScheduler`: 设备数据生成
- `PumpDeviceDataScheduler`: 水泵设备数据
- `LightingDeviceStatusScheduler`: 照明设备状态

## 注意事项

1. 简化测试接口已配置为无需登录认证
2. 定时器频率较高，仅用于测试验证
3. 生产环境建议调整定时器频率
4. 模拟数据仅用于功能验证，不会保存到数据库