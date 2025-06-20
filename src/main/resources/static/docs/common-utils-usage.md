# 公共工具类使用指南

## 概述

`CommonUtils` 是一个统一的前端工具类，提供了标准化的异常处理、API请求、表单验证等常用功能，旨在提高代码复用性和一致性。

## 引入方式

在HTML页面中引入公共工具类：

```html
<script src="/common-utils.js"></script>
```

## 主要功能

### 1. 统一异常处理

#### 基础异常处理

```javascript
// 基本用法 - API请求错误，会显示用户提示
CommonUtils.handleError(error, '保存失败', this.showErrorToast);

// 只记录日志，不显示提示
CommonUtils.handleError(error, '加载失败');

// 强制不显示用户提示（比如静态资源加载失败）
CommonUtils.handleError(error, '资源加载失败', this.showErrorToast, false);
```

**智能错误过滤**：
- 自动识别静态资源加载错误（.js、.css、.ico等），不会显示用户提示
- 只有真正的API业务请求错误才会显示错误提示框
- 所有错误都会记录到控制台日志中

```javascript
// 示例
try {
    // 一些可能出错的操作
    throw new Error('网络错误');
} catch (error) {
    CommonUtils.handleError(error, '保存数据失败', (message) => {
        // 显示错误提示
        alert(message);
    });
}
```

#### API请求统一处理

```javascript
// 推荐使用方式 - 自动处理成功和失败
try {
    const response = await CommonUtils.apiRequest(
        { method: 'post', url: '/api/users', data: userData },
        '用户创建成功',
        '创建用户失败',
        (message) => showSuccessToast(message)
    );
    // 处理成功响应
    console.log(response.data);
} catch (error) {
    // 错误已自动处理，这里可以做额外的错误处理
}

// 不同HTTP方法的示例
// GET请求
const response = await CommonUtils.apiRequest(
    { method: 'get', url: '/api/users/123' },
    '',
    '获取用户信息失败',
    (message) => showErrorToast(message)
);

// PUT请求
const response = await CommonUtils.apiRequest(
    { method: 'put', url: '/api/users/123', data: updateData },
    '更新成功',
    '更新失败',
    (message) => showSuccessToast(message)
);

// DELETE请求
const response = await CommonUtils.apiRequest(
    { method: 'delete', url: '/api/users/123' },
    '删除成功',
    '删除失败',
    (message) => showSuccessToast(message)
);
```

### 2. 表单验证

```javascript
// 定义验证规则
const rules = {
    username: {
        required: true,
        minLength: 3,
        maxLength: 20,
        message: '用户名不能为空'
    },
    email: {
        required: true,
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        patternMessage: '邮箱格式不正确'
    },
    password: {
        required: true,
        minLength: 6,
        message: '密码不能为空'
    }
};

// 验证表单数据
const formData = {
    username: 'john',
    email: 'john@example.com',
    password: '123456'
};

const validation = CommonUtils.validateForm(formData, rules);
if (!validation.valid) {
    validation.errors.forEach(error => {
        console.log(`${error.field}: ${error.message}`);
    });
}
```

### 3. 工具函数

#### 日期格式化

```javascript
// 默认格式 YYYY-MM-DD HH:mm:ss
const formatted = CommonUtils.formatDate(new Date());
// 输出: 2024-01-15 14:30:25

// 自定义格式
const customFormat = CommonUtils.formatDate(new Date(), 'YYYY-MM-DD');
// 输出: 2024-01-15
```

#### 防抖和节流

```javascript
// 防抖 - 延迟执行，多次调用只执行最后一次
const debouncedSearch = CommonUtils.debounce((keyword) => {
    // 搜索逻辑
    console.log('搜索:', keyword);
}, 300);

// 节流 - 限制执行频率
const throttledScroll = CommonUtils.throttle(() => {
    // 滚动处理逻辑
    console.log('滚动事件');
}, 100);
```

#### 深拷贝

```javascript
const original = { a: 1, b: { c: 2 } };
const copied = CommonUtils.deepClone(original);
copied.b.c = 3;
console.log(original.b.c); // 仍然是 2
```

## 在现有页面中的应用

### 替换现有异常处理

**原来的代码：**
```javascript
try {
    const response = await axios.post('/api/data', formData);
    if (response.data.code === 200) {
        showSuccessToast('保存成功');
    } else {
        showErrorToast('保存失败: ' + response.data.message);
    }
} catch (error) {
    console.error('保存失败:', error);
    showErrorToast('保存失败: ' + (error.response?.data?.message || error.message));
}
```

**使用工具类后：**
```javascript
try {
    await CommonUtils.apiRequest(
        { method: 'post', url: '/api/data', data: formData },
        '保存成功',
        '保存失败',
        (message) => showSuccessToast(message)
    );
} catch (error) {
    // 错误已自动处理
}
```

### Vue.js 项目中的使用

```javascript
const { createApp } = Vue;
createApp({
    data() {
        return {
            users: [],
            loading: false
        };
    },
    methods: {
        async loadUsers() {
            this.loading = true;
            try {
                const response = await CommonUtils.apiRequest(
                    { method: 'get', url: '/api/users' },
                    '',
                    '加载用户列表失败',
                    (message) => this.showErrorToast(message)
                );
                this.users = response.data;
            } catch (error) {
                // 错误已处理
            } finally {
                this.loading = false;
            }
        },
        
        async saveUser(userData) {
            // 表单验证
            const validation = CommonUtils.validateForm(userData, {
                name: { required: true, minLength: 2 },
                email: { required: true, pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ }
            });
            
            if (!validation.valid) {
                validation.errors.forEach(error => {
                    this.showErrorToast(error.message);
                });
                return;
            }
            
            // 保存数据
            try {
                await CommonUtils.apiRequest(
                    { method: 'post', url: '/api/users', data: userData },
                    '用户保存成功',
                    '保存用户失败',
                    (message) => this.showSuccessToast(message)
                );
                this.loadUsers(); // 重新加载列表
            } catch (error) {
                // 错误已处理
            }
        },
        
        showSuccessToast(message) {
            // 显示成功提示的实现
        },
        
        showErrorToast(message) {
            // 显示错误提示的实现
        }
    }
}).mount('#app');
```

## 最佳实践

1. **统一错误处理**：所有API调用都使用 `CommonUtils.apiRequest` 方法
2. **一致的提示方式**：在整个应用中使用相同的成功/错误提示组件
3. **表单验证**：使用 `CommonUtils.validateForm` 进行客户端验证
4. **性能优化**：在搜索、滚动等高频操作中使用防抖和节流
5. **代码复用**：将常用的工具函数抽取到 CommonUtils 中

## 扩展工具类

如果需要添加新的工具方法，可以直接在 `common-utils.js` 文件中扩展：

```javascript
class CommonUtils {
    // 现有方法...
    
    /**
     * 新增的工具方法
     */
    static newUtilMethod(param) {
        // 实现逻辑
    }
}
```

## 注意事项

1. 确保在使用前已正确引入 `common-utils.js` 文件
2. `apiRequest` 方法依赖 axios 库，确保已引入
3. 错误处理回调函数需要根据具体的UI框架进行适配
4. 表单验证规则可以根据业务需求进行扩展