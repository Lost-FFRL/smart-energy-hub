# Smart Energy Hub API 文档

## 📖 概述

Smart Energy Hub 提供完整的 RESTful API 服务，支持设备管理、数据查询、能源分析等功能。所有 API 接口遵循统一的响应格式，并提供完整的权限控制。

## 🔗 基础信息

- **Base URL**: `http://localhost:8080`
- **API 版本**: v1
- **数据格式**: JSON
- **字符编码**: UTF-8
- **API 文档**: `/swagger-ui.html`

## 🔐 认证授权

### 认证方式

项目使用 Sa-Token 框架进行权限认证：

```http
Authorization: Bearer {token}
```

### 获取Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expire": 7200
  }
}
```

## 📊 统一响应格式

所有 API 接口都使用统一的响应格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 | 描述 |
|--------|------|------|
| 200 | 成功 | 请求处理成功 |
| 400 | 请求错误 | 参数错误或请求格式不正确 |
| 401 | 未授权 | 需要登录或token无效 |
| 403 | 禁止访问 | 权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 内部服务器错误 |

## 🏠 系统管理 API

### 健康检查

检查系统运行状态。

```http
GET /api/simple-test/health
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "UP",
    "timestamp": "2025-06-25 10:00:00",
    "service": "Smart Energy Hub"
  }
}
```

### 系统状态

获取系统详细状态信息。

```http
GET /api/simple-test/status
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "currentTime": "2025-06-25 10:00:00",
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
      "description": "定时任务系统正常运行"
    }
  }
}
```

## 🔧 设备管理 API

### 设备列表查询

分页查询设备列表。

```http
GET /api/devices?page=1&size=10&deviceName=pump&regionId=1
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页大小，默认10 |
| deviceName | string | 否 | 设备名称（模糊查询） |
| regionId | long | 否 | 区域ID |

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "deviceCode": "PUMP_001",
        "deviceName": "1号水泵",
        "deviceType": "PUMP",
        "onlineStatus": 1,
        "workStatus": 1,
        "alarmStatus": 0,
        "lastOnlineTime": "2025-06-25 10:00:00",
        "regionId": 1,
        "regionName": "A区"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 设备详情

获取设备详细信息。

```http
GET /api/devices/{id}
```

### 设备统计

获取设备统计信息。

```http
GET /api/devices/statistics?regionId=1
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalCount": 100,
    "onlineCount": 95,
    "offlineCount": 5,
    "workingCount": 80,
    "alarmCount": 3,
    "onlineRate": 95.0,
    "offlineRate": 5.0,
    "alarmRate": 3.0
  }
}
```

## 📊 数据监控 API

### 流量监控数据

查询设备流量监控数据。

```http
GET /api/flow-monitor?deviceId=1&startTime=2025-06-25 00:00:00&endTime=2025-06-25 23:59:59
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | long | 是 | 设备ID |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| page | int | 否 | 页码 |
| size | int | 否 | 每页大小 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "deviceId": 1,
        "instantFlow": 125.5,
        "totalFlow": 1250.8,
        "flowVelocity": 2.5,
        "temperature": 22.5,
        "pressure": 0.45,
        "dataQuality": "GOOD",
        "monitorTime": "2025-06-25 10:00:00"
      }
    ],
    "total": 1440,
    "current": 1,
    "pages": 144
  }
}
```

### 液位监控数据

查询设备液位监控数据。

```http
GET /api/level-monitor?deviceId=1&startTime=2025-06-25 00:00:00&endTime=2025-06-25 23:59:59
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "deviceId": 1,
        "currentLevel": 3.2,
        "maxLevel": 5.0,
        "levelPercent": 64.0,
        "volume": 640.0,
        "temperature": 18.5,
        "density": 1.0,
        "alarmStatus": "NORMAL",
        "monitorTime": "2025-06-25 10:00:00"
      }
    ],
    "total": 1440,
    "current": 1,
    "pages": 144
  }
}
```

## ⚡ 能源分析 API

### 能耗统计

获取指定时间范围内的能耗统计数据。

```http
GET /api/energy/consumption?regionId=1&startDate=2025-06-01&endDate=2025-06-30&type=DAILY
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| regionId | long | 否 | 区域ID |
| startDate | string | 是 | 开始日期 |
| endDate | string | 是 | 结束日期 |
| type | string | 否 | 统计类型：DAILY/MONTHLY/YEARLY |

### 设备能效分析

分析设备能效数据。

```http
GET /api/energy/efficiency?deviceId=1&period=30
```

## 🎯 定时任务 API

### 手动触发定时任务

手动触发数据生成任务（仅用于测试）。

```http
POST /api/simple-test/trigger-scheduler
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": "定时任务已手动触发"
}
```

### 查看定时任务状态

```http
GET /api/scheduler/status
```

## 🔍 查询参数说明

### 分页参数

所有列表查询接口都支持分页参数：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 当前页码 |
| size | int | 10 | 每页记录数 |

### 时间格式

所有时间参数都使用以下格式：
- 日期时间：`yyyy-MM-dd HH:mm:ss`
- 日期：`yyyy-MM-dd`

### 排序参数

支持排序的接口可以使用以下参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| sortBy | string | 排序字段 |
| sortOrder | string | 排序方向：ASC/DESC |

## 🚨 错误处理

### 常见错误响应

**参数错误**:
```json
{
  "code": 400,
  "msg": "参数错误：deviceId不能为空",
  "data": null
}
```

**权限不足**:
```json
{
  "code": 403,
  "msg": "权限不足，无法访问该资源",
  "data": null
}
```

**资源不存在**:
```json
{
  "code": 404,
  "msg": "设备不存在",
  "data": null
}
```

## 📝 使用示例

### JavaScript/Fetch

```javascript
// 获取设备列表
fetch('/api/devices?page=1&size=10', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    console.log('设备列表:', data.data.records);
  }
});
```

### cURL

```bash
# 健康检查
curl -X GET http://localhost:8080/api/simple-test/health

# 获取设备统计（需要认证）
curl -X GET \
  -H "Authorization: Bearer {token}" \
  http://localhost:8080/api/devices/statistics
```

### Python/Requests

```python
import requests

# 设置基础URL和认证头
base_url = 'http://localhost:8080'
headers = {
    'Authorization': f'Bearer {token}',
    'Content-Type': 'application/json'
}

# 获取设备列表
response = requests.get(
    f'{base_url}/api/devices',
    headers=headers,
    params={'page': 1, 'size': 10}
)

if response.status_code == 200:
    data = response.json()
    if data['code'] == 200:
        devices = data['data']['records']
        print(f'找到 {len(devices)} 个设备')
```

## 🔄 版本更新

当前 API 版本：**v1.0.0**

### 版本兼容性

- 主版本号变更：不兼容的 API 修改
- 次版本号变更：向下兼容的功能新增
- 修订号变更：向下兼容的问题修正

### 废弃通知

废弃的 API 接口会在响应头中添加警告信息：

```http
Warning: 299 - "This API is deprecated and will be removed in v2.0.0"
```

## 📞 技术支持

- **API 文档**: http://localhost:8080/swagger-ui.html
- **问题反馈**: [GitHub Issues](https://github.com/your-username/smart-energy-hub/issues)
- **技术讨论**: [GitHub Discussions](https://github.com/your-username/smart-energy-hub/discussions)

---

**注意**: 本文档基于当前版本编写，具体接口实现可能会有调整。建议结合 Swagger 文档使用，获取最新的接口信息。