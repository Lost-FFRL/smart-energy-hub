# 贡献指南 🤝

感谢您对 Smart Energy Hub 项目的关注！我们欢迎所有形式的贡献，无论是代码、文档、测试还是反馈建议。

## 📋 贡献方式

### 🐛 报告 Bug

如果您发现了 Bug，请通过以下步骤报告：

1. 在 [Issues](https://github.com/your-username/smart-energy-hub/issues) 页面搜索是否已有相关问题
2. 如果没有，请创建新的 Issue，包含：
   - 清晰的问题描述
   - 复现步骤
   - 预期行为和实际行为
   - 环境信息（操作系统、Java版本、数据库版本等）
   - 相关的错误日志或截图

### 💡 功能建议

我们欢迎新功能建议：

1. 在 Issues 中创建 Feature Request
2. 详细描述功能需求和使用场景
3. 如果可能，提供设计思路或实现方案

### 📝 改进文档

文档改进包括：
- 修复错别字或语法错误
- 添加缺失的说明
- 改进代码示例
- 翻译文档

## 🔧 代码贡献

### 开发环境设置

1. **Fork 项目**
   ```bash
   # 在 GitHub 上 Fork 项目，然后克隆到本地
   git clone https://github.com/your-username/smart-energy-hub.git
   cd smart-energy-hub
   ```

2. **设置上游仓库**
   ```bash
   git remote add upstream https://github.com/original-owner/smart-energy-hub.git
   ```

3. **安装依赖**
   ```bash
   ./mvnw clean install
   ```

4. **配置开发环境**
   - 复制配置模板：`cp src/main/resources/application.properties.template src/main/resources/application.properties`
   - 配置数据库连接信息
   - 启动应用：`./mvnw spring-boot:run`

### 开发流程

1. **创建功能分支**
   ```bash
   git checkout -b feature/your-feature-name
   # 或者修复分支
   git checkout -b fix/issue-number
   ```

2. **编写代码**
   - 遵循项目的代码规范
   - 添加必要的测试
   - 确保所有测试通过

3. **提交代码**
   ```bash
   git add .
   git commit -m "feat: add new feature description"
   # 或者
   git commit -m "fix: resolve issue #123"
   ```

4. **推送分支**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **创建 Pull Request**
   - 在 GitHub 上创建 PR
   - 填写 PR 模板
   - 等待代码审查

### 代码规范

#### Java 代码规范

- 使用 4 个空格缩进
- 类名使用 PascalCase
- 方法名和变量名使用 camelCase
- 常量使用 UPPER_SNAKE_CASE
- 添加适当的注释和 JavaDoc

#### 提交信息规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**类型 (type)**：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

**示例**：
```
feat(scheduler): add energy consumption calculation

fix(api): resolve null pointer exception in device data endpoint

docs: update installation guide for Docker deployment
```

### 测试要求

- 为新功能添加单元测试
- 确保测试覆盖率不低于现有水平
- 运行所有测试确保通过：`./mvnw test`
- 如果添加了新的 API 端点，请添加集成测试

### 代码审查

所有 PR 都需要经过代码审查：

- 至少需要一个维护者的批准
- 确保 CI/CD 检查通过
- 解决审查中提出的问题
- 保持 PR 的简洁和专注

## 📦 发布流程

项目维护者负责版本发布：

1. 更新版本号
2. 更新 CHANGELOG.md
3. 创建 Git 标签
4. 发布 GitHub Release

## 🎯 开发优先级

当前开发重点：

1. **核心功能完善**
   - 设备数据采集优化
   - API 性能提升
   - 错误处理改进

2. **文档完善**
   - API 文档补充
   - 部署指南优化
   - 示例代码添加

3. **测试覆盖**
   - 单元测试补充
   - 集成测试添加
   - 性能测试

## 🤔 需要帮助？

如果您在贡献过程中遇到问题：

- 查看 [FAQ](https://github.com/your-username/smart-energy-hub/wiki/FAQ)
- 在 [Discussions](https://github.com/your-username/smart-energy-hub/discussions) 中提问
- 联系项目维护者

## 📜 行为准则

参与本项目即表示您同意遵守我们的行为准则：

- 尊重所有参与者
- 使用友好和包容的语言
- 接受建设性的批评
- 专注于对社区最有利的事情
- 对其他社区成员表现出同理心

## 🏆 贡献者认可

我们重视每一个贡献：

- 贡献者将被添加到项目的 Contributors 列表
- 重要贡献会在 Release Notes 中特别感谢
- 持续贡献者可能被邀请成为项目维护者

---

再次感谢您的贡献！每一个 PR、Issue 和建议都让这个项目变得更好。 🚀