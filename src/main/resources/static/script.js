// --- 0. 全局变量和 API 地址 ---
const API_BASE_URL = 'http://localhost:8080/api/todos';
let currentTodos = [];

// --- 1. DOM 元素获取 ---
const todoListElement = document.getElementById('todoList');
const searchInput = document.getElementById('searchInput');
const completedFilter = document.getElementById('completedFilter');
const sortBySelect = document.getElementById('sortBySelect');
const showAddTaskModalBtn = document.getElementById('showAddTaskModalBtn');
const loadingIndicator = document.getElementById('loadingIndicator');

// Modal Elements
const taskModal = document.getElementById('taskModal');
const closeModalBtn = taskModal.querySelector('.close-button');
const taskForm = document.getElementById('taskForm');
const modalTitle = document.getElementById('modalTitle');
const modalTodoId = document.getElementById('modalTodoId');
const modalTitleInput = document.getElementById('modalTitleInput');
const modalDescriptionInput = document.getElementById('modalDescriptionInput');
const modalPrioritySelect = document.getElementById('modalPrioritySelect');

// --- 2. 初始化和事件绑定 ---
document.addEventListener('DOMContentLoaded', () => {
    loadTodos();

    showAddTaskModalBtn.addEventListener('click', () => openModal());
    searchInput.addEventListener('input', loadTodos);
    completedFilter.addEventListener('change', loadTodos);
    sortBySelect.addEventListener('change', loadTodos);

    taskForm.addEventListener('submit', handleModalSubmit);
    closeModalBtn.addEventListener('click', closeModal);
    taskModal.addEventListener('click', (e) => {
        if (e.target === taskModal) closeModal();
    });

    todoListElement.addEventListener('click', handleListActions);
});

// --- 3. 核心 Fetch 函数 ---
async function executeFetch(url, method, body = null) {
    try {
        const options = {
            method: method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body) options.body = JSON.stringify(body);
        const response = await fetch(url, options);
        if (response.status === 204) return { success: true, data: null, status: 204 };
        if (!response.ok) {
            const errorBody = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(`HTTP Error ${response.status}: ${errorBody.message || 'Server Error'}`);
        }
        return { success: true, data: await response.json(), status: response.status };
    } catch (error) {
        console.error('API Error:', error);
        alert(`操作失败: ${error.message}`);
        return { success: false, data: null };
    }
}

// --- 4. 弹窗 (Modal) 管理 - 关键修复 ---
function openModal(todo = null) {
    if (todo) {
        modalTitle.textContent = '编辑任务';
        modalTodoId.value = todo.id;
        modalTitleInput.value = todo.title;
        modalDescriptionInput.value = todo.description;
        modalPrioritySelect.value = todo.priority;
    } else {
        modalTitle.textContent = '新增任务';
        taskForm.reset();
        modalTodoId.value = '';
        modalPrioritySelect.value = 'MEDIUM';
    }
    taskModal.classList.add('visible');
}

function closeModal() {
    taskModal.classList.remove('visible');
}

// --- 5. CRUD 和核心逻辑 ---
async function loadTodos() {
    loadingIndicator.style.display = 'flex';
    try {
        const search = searchInput.value.trim();
        const completed = completedFilter.value;
        const sortBy = sortBySelect.value;
        const params = new URLSearchParams();
        if (search) params.append('search', search);
        if (completed !== '') params.append('completed', completed);
        if (sortBy) params.append('sortBy', sortBy);
        const url = `${API_BASE_URL}?${params.toString()}`;
        const result = await executeFetch(url, 'GET');
        if (result.success && Array.isArray(result.data)) {
            currentTodos = result.data;
            renderTodoList(currentTodos);
        } else {
            todoListElement.innerHTML = '<div class="empty-state">糟糕！无法加载任务列表。</div>';
        }
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

async function handleModalSubmit(e) {
    e.preventDefault();
    const id = modalTodoId.value;
    const body = {
        title: modalTitleInput.value.trim(),
        description: modalDescriptionInput.value.trim(),
        priority: modalPrioritySelect.value,
    };
    if (!body.title) {
        alert('任务标题不能为空！');
        return;
    }
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;
    const result = await executeFetch(url, method, body);
    if (result.success) {
        closeModal();
        loadTodos();
    }
}

async function deleteTodo(id) {
    if (!confirm(`确定要删除此任务吗？`)) return;
    const result = await executeFetch(`${API_BASE_URL}/${id}`, 'DELETE');
    if (result.success) loadTodos();
}

async function toggleTodoStatus(id, isCompleted) {
    const result = await executeFetch(`${API_BASE_URL}/${id}`, 'PATCH', { completed: isCompleted });
    if (result.success) loadTodos();
}

// --- 6. DOM 渲染和辅助函数 ---
function getPriorityEmoji(priority) {
    switch (priority) {
        case 'HIGH': return '🔥';
        case 'MEDIUM': return '🟡';
        case 'LOW': return '❄️';
        default: return '';
    }
}

function formatRelativeTime(date) {
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);
    const minutes = Math.floor(diffInSeconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    if (diffInSeconds < 60) return '刚刚';
    if (minutes < 60) return `${minutes} 分钟前`;
    if (hours < 24) return `${hours} 小时前`;
    if (days === 1) return '昨天';
    if (days < 7) return `${days} 天前`;
    return date.toLocaleDateString('zh-CN');
}

function renderTodoList(todos) {
    todoListElement.innerHTML = '';
    if (todos.length === 0) {
        const hasFilters = searchInput.value || completedFilter.value !== '';
        const message = hasFilters
            ? '没有找到匹配的任务，请尝试更换筛选条件。'
            : '太棒了，所有任务都已完成！或者... 你可以 <a href="#" id="emptyStateAddLink">添加一个新任务</a>。';
        todoListElement.innerHTML = `<div class="empty-state">${message}</div>`;
        const emptyLink = document.getElementById('emptyStateAddLink');
        if (emptyLink) {
            emptyLink.onclick = (e) => {
                e.preventDefault();
                openModal();
            };
        }
        return;
    }
    todos.forEach(todo => {
        const createdDate = new Date(todo.createdAt);
        const updatedDate = new Date(todo.updatedAt);
        const updatedRelative = formatRelativeTime(updatedDate);
        const item = document.createElement('li');
        item.className = `todo-item ${todo.completed ? 'completed' : ''}`;
        item.dataset.id = todo.id;
        item.innerHTML = `
            <input type="checkbox" class="toggle-checkbox" data-action="toggle" data-id="${todo.id}" ${todo.completed ? 'checked' : ''}>
            <span class="priority-indicator" title="优先级: ${todo.priority}">${getPriorityEmoji(todo.priority)}</span>
            <div class="todo-item-content">
                <div class="todo-item-title">${todo.title}</div>
                ${todo.description ? `<div class="todo-item-description">${todo.description}</div>` : ''}
                <div class="todo-times">
                    <span>创建于: ${createdDate.toLocaleString('zh-CN')}</span>
                    <span>修改于: ${updatedRelative}</span>
                </div>
            </div>
            <div class="todo-actions">
                <button class="edit-btn" data-action="edit" data-id="${todo.id}">编辑</button>
                <button class="delete-btn" data-action="delete" data-id="${todo.id}">删除</button>
            </div>
        `;
        todoListElement.appendChild(item);
    });
}

function handleListActions(e) {
    const target = e.target;
    const action = target.dataset.action;
    const id = target.dataset.id;
    if (!action || !id) return;
    switch (action) {
        case 'delete':
            deleteTodo(id);
            break;
        case 'toggle':
            toggleTodoStatus(id, target.checked);
            break;
        case 'edit':
            const todoToEdit = currentTodos.find(t => t.id == id);
            if (todoToEdit) openModal(todoToEdit);
            break;
    }
}