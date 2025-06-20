// 全局变量
let currentPage = 1;
let pageSize = 10;
let totalPages = 0;
let isEditMode = false;
let currentEditId = null;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
    loadUserInfo();
    loadDevices();
    loadRegions(); // 加载区域列表
    
    // 绑定搜索事件
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchDevices();
        }
    });
});

// 检查用户认证
function checkAuth() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    // 设置请求头
    setAuthHeader(token);
}

// 设置认证头
function setAuthHeader(token) {
    // 为所有fetch请求设置默认头部
    const originalFetch = window.fetch;
    window.fetch = function(url, options = {}) {
        options.headers = {
            ...options.headers,
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        };
        return originalFetch(url, options);
    };
}

// 加载用户信息
function loadUserInfo() {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
        const user = JSON.parse(userInfo);
        document.getElementById('userWelcome').textContent = `欢迎，${user.realName || user.username}`;
    }
}

// 退出登录
function logout() {
    if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userInfo');
        window.location.href = '/login.html';
    }
}

// 加载设备列表
async function loadDevices(page = 1) {
    try {
        currentPage = page;
        const searchKeyword = document.getElementById('searchInput').value;
        const statusFilter = document.getElementById('statusFilter').value;
        const deviceTypeFilter = document.getElementById('deviceTypeFilter').value;
        
        let url = `/api/device?current=${page}&size=${pageSize}`;
        
        if (searchKeyword) {
            url += `&keyword=${encodeURIComponent(searchKeyword)}`;
        }
        
        if (statusFilter) {
            url += `&status=${statusFilter}`;
        }
        
        if (deviceTypeFilter) {
            url += `&deviceType=${deviceTypeFilter}`;
        }
        
        const response = await fetch(url);
        const result = await response.json();
        
        if (result.code === 200) {
            renderDeviceTable(result.data.records);
            renderPagination(result.data);
        } else {
            showError('加载设备列表失败：' + result.message);
        }
    } catch (error) {
        console.error('加载设备列表错误:', error);
        showError('网络错误，请稍后重试');
    }
}

// 渲染设备表格
function renderDeviceTable(devices) {
    const container = document.getElementById('deviceTableContainer');
    
    if (!devices || devices.length === 0) {
        container.innerHTML = '<div class="empty">暂无设备数据</div>';
        return;
    }
    
    const table = `
        <table class="table table-hover">
            <thead>
                <tr>
                    <th>设备编码</th>
                    <th>设备名称</th>
                    <th>设备类型</th>
                    <th>设备型号</th>
                    <th>生产厂商</th>
                    <th>在线状态</th>
                    <th>设备状态</th>
                    <th>安装日期</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                ${devices.map(device => `
                    <tr>
                        <td>${device.deviceCode || '-'}</td>
                        <td>${device.deviceName || '-'}</td>
                        <td>${getDeviceTypeText(device.deviceType)}</td>
                        <td>${device.deviceModel || '-'}</td>
                        <td>${device.manufacturer || '-'}</td>
                        <td><span class="status ${device.onlineStatus === 1 ? 'status-1' : 'status-0'}">${device.onlineStatus === 1 ? '在线' : '离线'}</span></td>
                        <td><span class="status status-${device.status}">${getStatusText(device.status)}</span></td>
                        <td>${device.installDate || '-'}</td>
                        <td>
                            <button class="btn btn-warning btn-sm" onclick="editDevice(${device.id})">编辑</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteDevice(${device.id}, '${device.deviceName}')">删除</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

// 获取状态文本
function getStatusText(status) {
    const statusMap = {
        1: '正常',
        0: '停用',
        2: '维修'
    };
    return statusMap[status] || '未知';
}

// 获取设备类型文本
function getDeviceTypeText(deviceType) {
    const typeMap = {
        'water': '水表',
        'electric': '电表',
        'gas': '气表',
        'heat': '热表'
    };
    return typeMap[deviceType] || deviceType || '-';
}

// 渲染分页
function renderPagination(pageData) {
    const container = document.getElementById('pagination');
    totalPages = pageData.pages;
    
    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }
    
    let pagination = '';
    
    // 上一页
    pagination += `<button ${currentPage <= 1 ? 'disabled' : ''} onclick="loadDevices(${currentPage - 1})">上一页</button>`;
    
    // 页码
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            pagination += `<button class="active">${i}</button>`;
        } else {
            pagination += `<button onclick="loadDevices(${i})">${i}</button>`;
        }
    }
    
    // 下一页
    pagination += `<button ${currentPage >= totalPages ? 'disabled' : ''} onclick="loadDevices(${currentPage + 1})">下一页</button>`;
    
    container.innerHTML = pagination;
}

// 搜索设备
function searchDevices() {
    loadDevices(1);
}

// 刷新设备列表
function refreshDevices() {
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    document.getElementById('deviceTypeFilter').value = '';
    loadDevices(1);
}

// 打开新增模态框
function openAddModal() {
    isEditMode = false;
    currentEditId = null;
    document.getElementById('modalTitle').textContent = '新增设备';
    document.getElementById('deviceForm').reset();
    document.getElementById('deviceCode').disabled = false;
    document.getElementById('deviceModal').style.display = 'block';
}

// 编辑设备
async function editDevice(id) {
    try {
        const response = await fetch(`/api/device/${id}`);
        const result = await response.json();
        
        if (result.code === 200) {
            isEditMode = true;
            currentEditId = id;
            document.getElementById('modalTitle').textContent = '编辑设备';
            
            const device = result.data;
            
            // 填充表单
            document.getElementById('deviceIdHidden').value = device.id;
            document.getElementById('deviceCode').value = device.deviceCode || '';
            document.getElementById('deviceName').value = device.deviceName || '';
            document.getElementById('deviceType').value = device.deviceType || '';
            document.getElementById('regionId').value = device.regionId || '';
            document.getElementById('deviceModel').value = device.deviceModel || '';
            document.getElementById('manufacturer').value = device.manufacturer || '';
            document.getElementById('installDate').value = device.installDate || '';
            document.getElementById('deviceAddr').value = device.deviceAddr || '';
            document.getElementById('communicationProtocol').value = device.communicationProtocol || '';
            document.getElementById('collectInterval').value = device.collectInterval || '';
            document.getElementById('unit').value = device.unit || '';
            document.getElementById('precisionDigits').value = device.precisionDigits || '';
            document.getElementById('multiplier').value = device.multiplier || '';
            document.getElementById('initialValue').value = device.initialValue || '';
            document.getElementById('status').value = device.status || 1;
            document.getElementById('remark').value = device.remark || '';
            
            // 编辑模式下设备编码不可修改
            document.getElementById('deviceCode').disabled = true;
            
            document.getElementById('deviceModal').style.display = 'block';
        } else {
            showError('获取设备信息失败：' + result.message);
        }
    } catch (error) {
        console.error('获取设备信息错误:', error);
        showError('网络错误，请稍后重试');
    }
}

// 保存设备
async function saveDevice() {
    try {
        const form = document.getElementById('deviceForm');
        const formData = new FormData(form);
        
        const deviceData = {
            deviceCode: formData.get('deviceCode'),
            deviceName: formData.get('deviceName'),
            deviceType: formData.get('deviceType'),
            regionId: formData.get('regionId') ? parseInt(formData.get('regionId')) : null,
            deviceModel: formData.get('deviceModel'),
            manufacturer: formData.get('manufacturer'),
            installDate: formData.get('installDate'),
            deviceAddr: formData.get('deviceAddr'),
            communicationProtocol: formData.get('communicationProtocol'),
            collectInterval: formData.get('collectInterval') ? parseInt(formData.get('collectInterval')) : null,
            unit: formData.get('unit'),
            precisionDigits: formData.get('precisionDigits') ? parseInt(formData.get('precisionDigits')) : null,
            multiplier: formData.get('multiplier') ? parseFloat(formData.get('multiplier')) : null,
            initialValue: formData.get('initialValue') ? parseFloat(formData.get('initialValue')) : null,
            status: parseInt(formData.get('status')),
            remark: formData.get('remark')
        };
        
        let url = '/api/device';
        let method = 'POST';
        
        if (isEditMode) {
            url = `/api/device/${currentEditId}`;
            method = 'PUT';
            deviceData.id = currentEditId;
        }
        
        const response = await fetch(url, {
            method: method,
            body: JSON.stringify(deviceData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showSuccess(isEditMode ? '设备更新成功' : '设备创建成功');
            closeModal();
            loadDevices(currentPage);
        } else {
            showError((isEditMode ? '设备更新失败：' : '设备创建失败：') + result.message);
        }
    } catch (error) {
        console.error('保存设备错误:', error);
        showError('网络错误，请稍后重试');
    }
}

// 删除设备
async function deleteDevice(id, deviceName) {
    if (!confirm(`确定要删除设备 "${deviceName}" 吗？此操作不可恢复。`)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/device/${id}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showSuccess('设备删除成功');
            loadDevices(currentPage);
        } else {
            showError('设备删除失败：' + result.message);
        }
    } catch (error) {
        console.error('删除设备错误:', error);
        showError('网络错误，请稍后重试');
    }
}

// 关闭模态框
function closeModal() {
    document.getElementById('deviceModal').style.display = 'none';
    document.getElementById('deviceForm').reset();
    isEditMode = false;
    currentEditId = null;
}

// 显示成功消息
function showSuccess(message) {
    // 简单的成功提示，可以后续改为更美观的提示组件
    alert(message);
}

// 显示错误消息
function showError(message) {
    // 简单的错误提示，可以后续改为更美观的提示组件
    alert(message);
}

// 加载区域列表
async function loadRegions() {
    try {
        const response = await fetch('/api/regions/tree');
        const result = await response.json();
        
        if (result.code === 200) {
            const regionSelect = document.getElementById('regionId');
            regionSelect.innerHTML = '<option value="">请选择区域</option>';
            
            // 递归添加区域选项
            function addRegionOptions(regions, level = 0) {
                regions.forEach(region => {
                    const option = document.createElement('option');
                    option.value = region.id;
                    option.textContent = '　'.repeat(level) + region.regionName;
                    regionSelect.appendChild(option);
                    
                    // 如果有子区域，递归添加
                    if (region.children && region.children.length > 0) {
                        addRegionOptions(region.children, level + 1);
                    }
                });
            }
            
            addRegionOptions(result.data);
        } else {
            console.error('加载区域列表失败：', result.message);
        }
    } catch (error) {
        console.error('加载区域列表错误:', error);
    }
}

// 点击模态框外部关闭
window.onclick = function(event) {
    const modal = document.getElementById('deviceModal');
    if (event.target === modal) {
        closeModal();
    }
}