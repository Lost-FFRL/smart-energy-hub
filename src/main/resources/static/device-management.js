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
        
        let url = `/api/device/page?current=${page}&size=${pageSize}`;
        
        if (searchKeyword) {
            url += `&keyword=${encodeURIComponent(searchKeyword)}`;
        }
        
        if (statusFilter) {
            url += `&status=${statusFilter}`;
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
        <table class="table">
            <thead>
                <tr>
                    <th>设备ID</th>
                    <th>设备名称</th>
                    <th>设备类型</th>
                    <th>状态</th>
                    <th>安装位置</th>
                    <th>负责人</th>
                    <th>联系电话</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                ${devices.map(device => `
                    <tr>
                        <td>${device.deviceId || '-'}</td>
                        <td>${device.deviceName || '-'}</td>
                        <td>${device.deviceType || '-'}</td>
                        <td><span class="status status-${device.status}">${getStatusText(device.status)}</span></td>
                        <td>${device.location || '-'}</td>
                        <td>${device.responsiblePerson || '-'}</td>
                        <td>${device.contactPhone || '-'}</td>
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
        'online': '在线',
        'offline': '离线',
        'maintenance': '维护中'
    };
    return statusMap[status] || status;
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
    loadDevices(1);
}

// 打开新增模态框
function openAddModal() {
    isEditMode = false;
    currentEditId = null;
    document.getElementById('modalTitle').textContent = '新增设备';
    document.getElementById('deviceForm').reset();
    document.getElementById('deviceId').disabled = false;
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
            document.getElementById('deviceId').value = device.deviceId || '';
            document.getElementById('deviceName').value = device.deviceName || '';
            document.getElementById('deviceType').value = device.deviceType || '';
            document.getElementById('deviceModel').value = device.deviceModel || '';
            document.getElementById('manufacturer').value = device.manufacturer || '';
            document.getElementById('installationDate').value = device.installationDate || '';
            document.getElementById('status').value = device.status || 'online';
            document.getElementById('location').value = device.location || '';
            document.getElementById('ratedPower').value = device.ratedPower || '';
            document.getElementById('ratedVoltage').value = device.ratedVoltage || '';
            document.getElementById('ratedCurrent').value = device.ratedCurrent || '';
            document.getElementById('areaCode').value = device.areaCode || '';
            document.getElementById('areaName').value = device.areaName || '';
            document.getElementById('responsiblePerson').value = device.responsiblePerson || '';
            document.getElementById('contactPhone').value = device.contactPhone || '';
            document.getElementById('maintenanceCycle').value = device.maintenanceCycle || '';
            document.getElementById('warrantyUntil').value = device.warrantyUntil || '';
            document.getElementById('remark').value = device.remark || '';
            
            // 编辑模式下设备ID不可修改
            document.getElementById('deviceId').disabled = true;
            
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
            deviceId: formData.get('deviceId'),
            deviceName: formData.get('deviceName'),
            deviceType: formData.get('deviceType'),
            deviceModel: formData.get('deviceModel'),
            manufacturer: formData.get('manufacturer'),
            installationDate: formData.get('installationDate'),
            status: formData.get('status'),
            location: formData.get('location'),
            ratedPower: formData.get('ratedPower') ? parseFloat(formData.get('ratedPower')) : null,
            ratedVoltage: formData.get('ratedVoltage') ? parseFloat(formData.get('ratedVoltage')) : null,
            ratedCurrent: formData.get('ratedCurrent') ? parseFloat(formData.get('ratedCurrent')) : null,
            areaCode: formData.get('areaCode'),
            areaName: formData.get('areaName'),
            responsiblePerson: formData.get('responsiblePerson'),
            contactPhone: formData.get('contactPhone'),
            maintenanceCycle: formData.get('maintenanceCycle') ? parseInt(formData.get('maintenanceCycle')) : null,
            warrantyUntil: formData.get('warrantyUntil'),
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

// 点击模态框外部关闭
window.onclick = function(event) {
    const modal = document.getElementById('deviceModal');
    if (event.target === modal) {
        closeModal();
    }
}