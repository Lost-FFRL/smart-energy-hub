# 设备读数API文档

## 1. 获取用水量趋势数据

**请求URL**: `GET /api/device-readings/water-usage-trend`

**请求参数**:
- `deviceId`: 设备ID (必填)
- `startTime`: 开始时间 (格式: yyyy-MM-dd HH:mm:ss)
- `endTime`: 结束时间 (格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "deviceId": 1,
    "deviceName": "1号水表",
    "points": [
      {
        "time": "2023-06-01T10:00:00",
        "value": 15.2,
        "yesterdayValue": 14.8
      },
      {
        "time": "2023-06-01T11:00:00",
        "value": 16.5,
        "yesterdayValue": 15.3
      }
    ]
  }
}
```

## 2. 获取告警阈值

**请求URL**: `GET /api/device-readings/alert-threshold`

**请求参数**:
- `deviceId`: 设备ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 20.5
}
```

## 3. 获取用电量对比数据

**请求URL**: `GET /api/device-readings/electricity-comparison`

**请求参数**:
- `deviceId`: 设备ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "deviceId": 2,
    "deviceName": "1号电表",
    "currentHourUsage": 120.5,
    "previousHourUsage": 115.3,
    "samePeriodLastYearUsage": 105.8,
    "hourToHourRate": 4.5,
    "yearOverYearRate": 13.9
  }
}
```

## 性能优化建议
1. 确保`device_readings`表有以下索引:
   - `(device_id, reading_time)`
   - `(device_id, reading_time, current_value)`
2. 大数据量查询建议添加时间范围限制
3. 频繁查询可考虑使用缓存
