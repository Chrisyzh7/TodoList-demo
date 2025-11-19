const API_BASE_URL = 'http://localhost:8080/api/todos';
const todoListElement = document.getElementById('todoList');
const addTodoForm = document.getElementById('addTodoForm');
const searchButton = document.getElementById('searchButton');
const resetButton = document.getElementById('resetButton');
const searchInput = document.getElementById('searchInput');
const completedFilter = document.getElementById('completedFilter');

// --- 1. 初始化和事件绑定 ---

document.addEventListener('DOMContentLoaded', () => {
    loadTodos();

    // 监听新增任务表单提交
    addTodoForm.addEventListener('submit', createTodo);

    // 监听搜索按钮点击
    searchButton.addEventListener('click', loadTodos);

    // 监听重置按钮点击
    resetButton.addEventListener('click', resetAndLoadTodos);

    // 监听任务列表的点击事件 (使用事件委托)
    todoListElement.addEventListener('click', handleListActions);
});

// --- 2. 核心 Fetch 函数 ---

/**
 * 封装 Fetch API，处理 JSON 数据
 */
async function executeFetch(url, method, body = null) {
    try {
        const options = {
            method: method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body) {
            options.body = JSON.stringify(body);
        }

        const response = await fetch(url, options);

        // 专门处理 204 No Content，它没有响应体
        if (response.status === 204) {
            return { success: true, data: null, status: 204 };
        }

        if (!response.ok) {
            // 抛出包含状态码和错误信息的错误
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

// --- 3. CRUD 方法实现 ---

/**
 * [GET] /api/todos - 加载并显示所有待办事项，支持搜索和筛选
 */
async function loadTodos() {
    // 构造查询参数
    const search = searchInput.value.trim();
    const completed = completedFilter.value; // 'true', 'false', 或 ''

    let url = API_BASE_URL;
    const params = new URLSearchParams();

    if (search) {
        params.append('search', search);
    }
    if (completed !== '') {
        params.append('completed', completed);
    }

    if (params.toString()) {
        url += '?' + params.toString();
    }

    const result = await executeFetch(url, 'GET');

    if (result.success && Array.isArray(result.data)) {
        renderTodoList(result.data);
    } else {
        todoListElement.innerHTML = '<li style="color:red; text-align:center;">无法加载任务列表。</li>';
    }
}

/**
 * [POST] /api/todos - 创建新的待办事项
 */
async function createTodo(e) {
    e.preventDefault(); // 阻止表单默认提交行为

    const title = document.getElementById('newTitle').value.trim();
    const description = document.getElementById('newDescription').value.trim();

    if (!title) {
        alert('任务标题不能为空！');
        return;
    }

    const body = { title, description };
    const result = await executeFetch(API_BASE_URL, 'POST', body);

    if (result.success) {
        addTodoForm.reset(); // 清空表单
        loadTodos(); // 刷新列表
    }
}

/**
 * [DELETE] /api/todos/{id} - 删除待办事项
 */
async function deleteTodo(id) {
    if (!confirm(`确定要删除 ID: ${id} 的待办事项吗？`)) {
        return;
    }
    const result = await executeFetch(`${API_BASE_URL}/${id}`, 'DELETE');

    if (result.success) {
        loadTodos(); // 删除成功后刷新列表
    }
}

/**
 * [PATCH] /api/todos/{id} - 切换完成状态
 */
async function toggleTodoStatus(id, isCompleted) {
    const result = await executeFetch(`${API_BASE_URL}/${id}`, 'PATCH', {
        completed: !isCompleted // 切换状态
    });

    if (result.success) {
        loadTodos(); // 更新成功后刷新列表
    }
}

/**
 * 重置搜索和筛选条件
 */
function resetAndLoadTodos() {
    searchInput.value = '';
    completedFilter.value = '';
    loadTodos();
}

// --- 4. DOM 操作和事件委托 ---

/**
 * 渲染待办事项列表
 */
function renderTodoList(todos) {
    todoListElement.innerHTML = ''; // 清空列表

    if (todos.length === 0) {
        todoListElement.innerHTML = '<p style="text-align:center; color: var(--secondary-color);">没有找到匹配的任务。</p>';
        return;
    }

    todos.forEach(todo => {
        // --- 格式化时间 ---
        // 确保后端返回的时间戳 (或ISO字符串) 是有效的
        const createdDate = new Date(todo.createdAt);
        const updatedDate = new Date(todo.updatedAt);

        const timeOptions = {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', second: '2-digit' // 添加秒级显示
        };

        // 使用 toLocaleString 进行本地化格式化 (例如: "2025/11/19 15:00:00")
        const createdAt = createdDate.toLocaleString('zh-CN', timeOptions);
        const updatedAt = updatedDate.toLocaleString('zh-CN', timeOptions);
        // ------------------

        const item = document.createElement('li');
        item.className = `todo-item ${todo.completed ? 'completed' : ''}`;
        item.dataset.id = todo.id; // 存储 ID

        item.innerHTML = `
            <input type="checkbox" class="toggle-checkbox" ${todo.completed ? 'checked' : ''} data-id="${todo.id}">
            
            <div class="todo-item-content">
                <div class="todo-item-title">${todo.title}</div>
                <div class="todo-item-description">${todo.description || '无描述'}</div>
                
                <div class="todo-times">
                    <span class="todo-created-at">创建于: ${createdAt}</span>
                    <span class="todo-updated-at">修改于: ${updatedAt}</span>
                </div>
            </div>
            
            <div class="todo-actions">
                <button class="delete-btn" data-action="delete" data-id="${todo.id}">删除</button>
            </div>
        `;
        todoListElement.appendChild(item);
    });
}

/**
 * 使用事件委托处理列表项上的所有点击事件
 */
function handleListActions(e) {
    const target = e.target;
    const id = target.dataset.id;

    if (!id) return;

    // 监听删除按钮
    if (target.classList.contains('delete-btn') && target.dataset.action === 'delete') {
        deleteTodo(id);
        return;
    }

    // 监听复选框状态切换
    if (target.classList.contains('toggle-checkbox')) {
        // isCompleted 已经是切换后的新状态，但我们 PATCH 需要的是切换前状态的反向
        const isCompleted = target.checked;
        toggleTodoStatus(id, !isCompleted); // 传入 !isCompleted，让后端将状态切换为 isCompleted
    }
}