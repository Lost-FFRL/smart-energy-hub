/**
 * 公共工具类 - 统一异常处理和常用功能
 */
class CommonUtils {
    
    /**
     * 统一的错误处理方法
     * @param {Error} error - 错误对象
     * @param {string} defaultMessage - 默认错误消息
     * @param {Function} showToast - 显示提示的方法
     * @param {boolean} showUserToast - 是否显示用户提示，默认true
     */
    static handleError(error, defaultMessage = '操作失败', showToast = null, showUserToast = true) {
        console.error(defaultMessage + ':', error);
        
        let errorMessage = defaultMessage;
        
        // 检查是否是静态资源加载错误，这类错误不应该显示给用户
        const isStaticResourceError = this.isStaticResourceError(error);
        
        // 尝试从响应中获取具体错误信息
        if (error.response && error.response.data) {
            // 优先使用 message 字段
            if (error.response.data.message) {
                errorMessage = defaultMessage + ': ' + error.response.data.message;
            }
            // 兼容旧的 msg 字段
            else if (error.response.data.msg) {
                errorMessage = defaultMessage + ': ' + error.response.data.msg;
            }
            // 如果有 data 字段且是字符串
            else if (typeof error.response.data === 'string') {
                errorMessage = defaultMessage + ': ' + error.response.data;
            }
        }
        // 使用错误对象的 message
        else if (error.message) {
            errorMessage = defaultMessage + ': ' + error.message;
        }
        
        // 只有在非静态资源错误且允许显示用户提示时才显示Toast
        if (!isStaticResourceError && showUserToast && showToast && typeof showToast === 'function') {
            showToast(errorMessage);
        }
        
        return errorMessage;
    }
    
    /**
     * 判断是否是静态资源加载错误
     * @param {Error} error - 错误对象
     * @returns {boolean}
     */
    static isStaticResourceError(error) {
        // 检查请求URL是否是静态资源
        if (error.config && error.config.url) {
            const url = error.config.url.toLowerCase();
            const staticExtensions = ['.js', '.css', '.ico', '.png', '.jpg', '.jpeg', '.gif', '.svg', '.woff', '.woff2', '.ttf', '.eot'];
            return staticExtensions.some(ext => url.endsWith(ext));
        }
        
        // 检查错误消息是否包含静态资源相关关键词
        if (error.message) {
            const message = error.message.toLowerCase();
            const staticKeywords = ['favicon', 'static resource', '.js', '.css', '.ico'];
            return staticKeywords.some(keyword => message.includes(keyword));
        }
        
        return false;
    }
    
    /**
     * 统一的成功处理方法
     * @param {string} message - 成功消息
     * @param {Function} showToast - 显示提示的方法
     */
    static handleSuccess(message = '操作成功', showToast = null) {
        if (showToast && typeof showToast === 'function') {
            showToast(message);
        }
    }
    
    /**
     * 统一的API请求方法
     * @param {Object} config - axios配置对象
     * @param {string} successMessage - 成功提示消息
     * @param {string} errorMessage - 错误提示消息
     * @param {Function} showToast - 显示提示的方法
     * @returns {Promise}
     */
    static async apiRequest(config, successMessage = '操作成功', errorMessage = '操作失败', showToast = null) {
        try {
            const response = await axios(config);
            
            // 检查响应状态
            if (response.data && response.data.code === 200) {
                this.handleSuccess(successMessage, showToast);
                return response.data;
            } else {
                // 业务错误
                const businessError = new Error(response.data.message || '业务处理失败');
                businessError.response = response;
                throw businessError;
            }
        } catch (error) {
            this.handleError(error, errorMessage, showToast);
            throw error;
        }
    }
    
    /**
     * 格式化日期
     * @param {Date|string} date - 日期对象或字符串
     * @param {string} format - 格式化模式，默认 'YYYY-MM-DD HH:mm:ss'
     * @returns {string}
     */
    static formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!date) return '';
        
        const d = new Date(date);
        if (isNaN(d.getTime())) return '';
        
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    }
    
    /**
     * 防抖函数
     * @param {Function} func - 要防抖的函数
     * @param {number} delay - 延迟时间（毫秒）
     * @returns {Function}
     */
    static debounce(func, delay = 300) {
        let timeoutId;
        return function (...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    }
    
    /**
     * 节流函数
     * @param {Function} func - 要节流的函数
     * @param {number} delay - 延迟时间（毫秒）
     * @returns {Function}
     */
    static throttle(func, delay = 300) {
        let lastCall = 0;
        return function (...args) {
            const now = Date.now();
            if (now - lastCall >= delay) {
                lastCall = now;
                return func.apply(this, args);
            }
        };
    }
    
    /**
     * 深拷贝对象
     * @param {any} obj - 要拷贝的对象
     * @returns {any}
     */
    static deepClone(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => this.deepClone(item));
        if (typeof obj === 'object') {
            const clonedObj = {};
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) {
                    clonedObj[key] = this.deepClone(obj[key]);
                }
            }
            return clonedObj;
        }
    }
    
    /**
     * 验证表单字段
     * @param {Object} data - 表单数据
     * @param {Object} rules - 验证规则
     * @returns {Object} {valid: boolean, errors: Array}
     */
    static validateForm(data, rules) {
        const errors = [];
        
        for (const field in rules) {
            const rule = rules[field];
            const value = data[field];
            
            // 必填验证
            if (rule.required && (!value || value.toString().trim() === '')) {
                errors.push({
                    field,
                    message: rule.message || `${field}不能为空`
                });
                continue;
            }
            
            // 如果值为空且不是必填，跳过其他验证
            if (!value && !rule.required) continue;
            
            // 长度验证
            if (rule.minLength && value.length < rule.minLength) {
                errors.push({
                    field,
                    message: `${field}长度不能少于${rule.minLength}个字符`
                });
            }
            
            if (rule.maxLength && value.length > rule.maxLength) {
                errors.push({
                    field,
                    message: `${field}长度不能超过${rule.maxLength}个字符`
                });
            }
            
            // 正则验证
            if (rule.pattern && !rule.pattern.test(value)) {
                errors.push({
                    field,
                    message: rule.patternMessage || `${field}格式不正确`
                });
            }
        }
        
        return {
            valid: errors.length === 0,
            errors
        };
    }
}

// 如果在浏览器环境中，将工具类挂载到全局
if (typeof window !== 'undefined') {
    window.CommonUtils = CommonUtils;
}