# 开发指南

本文档包含 Smart Energy Hub 项目的开发相关信息，包括热重载配置、数据库迁移、功能验证等内容。

## 🔥 热重载配置

项目已配置 Spring Boot DevTools 热更新功能，可以在开发过程中自动重启应用，提高开发效率。

### 功能特性

- **自动重启**：当 Java 代码或配置文件发生变化时自动重启应用
- **LiveReload**：支持浏览器自动刷新（需要浏览器插件）
- **快速重启**：只重启应用上下文，比完全重启更快
- **智能排除**：排除静态资源、日志文件等不需要重启的文件

### 使用方法

#### 1. 启动应用

```bash
# 使用 Maven 启动（推荐开发环境）
mvn spring-boot:run

# 或使用开发环境配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或直接运行 jar 包
java -jar target/smart-energy-hub-1.0.0.jar --spring.profiles.active=dev
```

#### 2. 开发过程中

- **修改 Java 代码**：保存文件后，应用会在 1-2 秒内自动重启
- **修改配置文件**：保存 `.properties` 或 `.yml` 文件后自动重启
- **修改模板文件**：Thymeleaf 模板文件修改后会立即生效（无需重启）
- **修改静态资源**：CSS、JS、图片等静态资源修改后立即生效

#### 3. LiveReload（可选）

安装浏览器插件以支持页面自动刷新：
- Chrome: [LiveReload Extension](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
- Firefox: [LiveReload Add-on](https://addons.mozilla.org/en-US/firefox/addon/livereload/)

安装后点击插件图标启用，当文件变化时浏览器会自动刷新。

### 配置说明

#### 基础配置（application.properties）

```properties
# 启用热更新
spring.devtools.restart.enabled=true
# 监控的路径
spring.devtools.restart.additional-paths=src/main/java,src/main/resources
# 排除的路径
spring.devtools.restart.exclude=static/**,public/**
# 启用 LiveReload
spring.devtools.livereload.enabled=true
```

#### 开发环境配置（application-dev.properties）

开发环境有更优化的配置：
- 更快的文件检测间隔（500ms）
- 更短的静默期（200ms）
- 详细的调试日志
- 禁用模板缓存

### IDE 配置

**IntelliJ IDEA：**
- 确保启用 "Build project automatically"
- 在 Settings → Build → Compiler 中勾选 "Build project automatically"
- 按 Ctrl+Shift+A 搜索 "Registry"，启用 "compiler.automake.allow.when.app.running"

**Eclipse：**
- 默认支持自动编译，无需额外配置

**VS Code：**
- 安装 Java Extension Pack
- 确保自动保存功能开启

## 🗄️ 数据库迁移

项目使用 Flyway 数据库迁移工具，用于管理数据库表结构的版本控制和自动化部署。

### 配置说明

#### 依赖配置

在 `pom.xml` 中已添加以下依赖：

```xml
<!-- Flyway 数据库迁移工具 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Flyway MySQL 驱动支持 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

#### 应用配置

在 `application.properties` 中的 Flyway 配置：

```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
spring.flyway.out-of-order=false
spring.flyway.table=flyway_schema_history
```

### 迁移脚本规范

#### 文件命名规则

迁移脚本必须遵循以下命名规范：

```
V{版本号}__{描述}.sql
```

示例：
- `V1__system_table.sql` - 创建 demo 表
- `V2__demo.sql` - 插入初始数据
- `V3__Add_user_table.sql` - 添加用户表
- `V4__Alter_demo_add_column.sql` - 修改 demo 表添加字段

#### 版本号规则

- 版本号必须是递增的
- 可以使用数字：`V1`, `V2`, `V3`
- 也可以使用点分版本：`V1.1`, `V1.2`, `V2.0`
- 一旦脚本被执行，不能修改已有的迁移脚本

#### 脚本存放位置

所有迁移脚本存放在：
```
src/main/resources/db/migration/
```

### 使用指南

#### 添加新的迁移脚本

1. 在 `src/main/resources/db/migration/` 目录下创建新的 SQL 文件
2. 按照命名规范命名文件（版本号要比现有的大）
3. 编写 SQL 脚本内容
4. 重启应用程序，Flyway 会自动执行新的迁移脚本

#### 示例：添加用户表

创建文件 `V3__Create_user_table.sql`：

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

#### 最佳实践

1. **向前兼容**：新的迁移脚本应该向前兼容，避免破坏现有数据
2. **原子性**：每个迁移脚本应该是原子性的，要么全部成功，要么全部失败
3. **测试**：在生产环境应用之前，先在开发和测试环境验证迁移脚本
4. **备份**：在执行重要的数据库迁移之前，务必备份数据库
5. **描述性命名**：使用描述性的文件名，便于理解迁移的目的

#### 常用 Flyway 命令

```bash
# 查看迁移状态
./mvnw flyway:info

# 验证迁移脚本
./mvnw flyway:validate

# 手动执行迁移（通常不需要，Spring Boot 会自动执行）
./mvnw flyway:migrate

# 清理数据库（谨慎使用，会删除所有数据）
./mvnw flyway:clean
```

### 注意事项

1. **不要修改已执行的迁移脚本**：一旦迁移脚本被执行，就不应该再修改它们
2. **版本控制**：所有迁移脚本都应该纳入版本控制系统
3. **团队协作**：团队成员应该协调版本号，避免冲突
4. **生产环境**：在生产环境部署前，确保所有迁移脚本都经过充分测试

## ✅ 功能验证

### 验证概述

项目的定时器功能和模拟数据生成已经过全面测试验证。

### 验证结果

#### 1. 定时器功能验证 - **通过**

**简化定时器测试 (SimpleSchedulerTest)**
- **每分钟定时器**: ✅ 正常执行
  - Cron表达式: `0 * * * * ?`
  - 功能: 生成模拟数据（温度、湿度、功率）
  - 日志示例: `[每分钟定时器] 执行时间: 2025-06-25 17:30:00`

- **每5分钟定时器**: ✅ 正常执行
  - Cron表达式: `0 */5 * * * ?`
  - 功能: 模拟设备状态变化（在线、工作、告警状态）
  - 日志示例: `[设备状态模拟] 在线: 否, 工作: 否, 告警: 是`

- **每10分钟定时器**: ✅ 正常执行
  - Cron表达式: `0 */10 * * * ?`
  - 功能: 模拟数据统计

#### 2. 模拟数据生成验证 - **通过**

**数据类型**
- **温度数据**: 20-30°C 范围内随机生成
- **湿度数据**: 40-80% 范围内随机生成
- **功率数据**: 100-600W 范围内随机生成
- **设备状态**: 在线率90%，工作率80%，告警率5%
- **统计数据**: 设备总数、在线数、告警数、数据量

**日志格式修复**
- ✅ 修复了Java日志格式化问题
- ✅ 将Python风格的`{:.2f}`改为Java的`{}`占位符
- ✅ 使用`String.format("%.2f", value)`进行数值格式化

#### 3. 应用启动验证 - **通过**

- ✅ Spring Boot应用正常启动
- ✅ Tomcat服务器在8081端口启动成功
- ✅ Sa-Token认证系统正常加载
- ✅ MyBatis Plus数据库连接正常
- ✅ 定时任务调度器正常初始化

### 性能表现

- **启动时间**: ~3秒
- **内存占用**: 正常范围
- **定时器精度**: 准确到秒级
- **日志输出**: 清晰且有序

### 配置优化建议

#### 1. Sa-Token配置优化
- 已添加`/api/simple-test/**`路径到排除列表
- 测试接口无需登录认证
- 保持核心业务接口的安全性

#### 2. 日志配置建议
```properties
# 建议的日志配置
logging.level.com.kfblue.seh.scheduler=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %-40.40logger{39} : %m%n
```

#### 3. 定时器配置建议
```properties
# 可选的定时器配置
spring.task.scheduling.pool.size=5
spring.task.scheduling.thread-name-prefix=scheduler-
```

## 🔧 故障排除

### 热更新问题

**热更新不工作：**
1. 检查 DevTools 依赖是否正确添加
2. 确认 IDE 的自动编译功能已启用
3. 查看控制台是否有 DevTools 相关日志
4. 尝试手动触发编译（Ctrl+F9 in IDEA）

**重启过于频繁：**
1. 检查 `exclude` 配置是否正确
2. 增加 `quiet-period` 的值
3. 确认没有其他进程在修改监控的文件

### 数据库迁移问题

**常见问题**
1. **迁移失败**：检查 SQL 语法和数据库连接
2. **版本冲突**：确保版本号是唯一且递增的
3. **权限问题**：确保数据库用户有足够的权限执行 DDL 操作

**日志查看**

启动应用时，可以在日志中看到 Flyway 的执行信息：

```properties
logging.level.org.flywaydb=INFO
```

通过以上配置，您可以在应用启动时看到 Flyway 的详细执行日志。

## 📚 相关文档

- [Spring Boot DevTools 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [LiveReload 官网](http://livereload.com/)

---

**注意**: 本开发指南适用于开发环境，生产环境部署请参考 [部署指南](../DEPLOYMENT.md)。