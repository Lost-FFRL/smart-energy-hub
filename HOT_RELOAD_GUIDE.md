# 热更新配置指南

本项目已配置 Spring Boot DevTools 热更新功能，可以在开发过程中自动重启应用，提高开发效率。

## 功能特性

- **自动重启**：当 Java 代码或配置文件发生变化时自动重启应用
- **LiveReload**：支持浏览器自动刷新（需要浏览器插件）
- **快速重启**：只重启应用上下文，比完全重启更快
- **智能排除**：排除静态资源、日志文件等不需要重启的文件

## 使用方法

### 1. 启动应用

```bash
# 使用 Maven 启动（推荐开发环境）
mvn spring-boot:run

# 或使用开发环境配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或直接运行 jar 包
java -jar target/smart-energy-hub-1.0.0.jar --spring.profiles.active=dev
```

### 2. 开发过程中

- **修改 Java 代码**：保存文件后，应用会在 1-2 秒内自动重启
- **修改配置文件**：保存 `.properties` 或 `.yml` 文件后自动重启
- **修改模板文件**：Thymeleaf 模板文件修改后会立即生效（无需重启）
- **修改静态资源**：CSS、JS、图片等静态资源修改后立即生效

### 3. LiveReload（可选）

安装浏览器插件以支持页面自动刷新：
- Chrome: [LiveReload Extension](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
- Firefox: [LiveReload Add-on](https://addons.mozilla.org/en-US/firefox/addon/livereload/)

安装后点击插件图标启用，当文件变化时浏览器会自动刷新。

## 配置说明

### 基础配置（application.properties）

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

### 开发环境配置（application-dev.properties）

开发环境有更优化的配置：
- 更快的文件检测间隔（500ms）
- 更短的静默期（200ms）
- 详细的调试日志
- 禁用模板缓存

## 注意事项

### 1. 生产环境

- DevTools 在生产环境会自动禁用
- 配置类使用 `@Profile("!prod")` 确保不在生产环境加载

### 2. IDE 配置

**IntelliJ IDEA：**
- 确保启用 "Build project automatically"
- 在 Settings → Build → Compiler 中勾选 "Build project automatically"
- 按 Ctrl+Shift+A 搜索 "Registry"，启用 "compiler.automake.allow.when.app.running"

**Eclipse：**
- 默认支持自动编译，无需额外配置

**VS Code：**
- 安装 Java Extension Pack
- 确保自动保存功能开启

### 3. 性能优化

- 如果重启过于频繁，可以增加 `poll-interval` 和 `quiet-period` 的值
- 大型项目可以通过 `exclude` 排除更多不必要的路径

### 4. 故障排除

**热更新不工作：**
1. 检查 DevTools 依赖是否正确添加
2. 确认 IDE 的自动编译功能已启用
3. 查看控制台是否有 DevTools 相关日志
4. 尝试手动触发编译（Ctrl+F9 in IDEA）

**重启过于频繁：**
1. 检查 `exclude` 配置是否正确
2. 增加 `quiet-period` 的值
3. 确认没有其他进程在修改监控的文件

## 相关文档

- [Spring Boot DevTools 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [LiveReload 官网](http://livereload.com/)