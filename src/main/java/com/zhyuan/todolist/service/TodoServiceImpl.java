package com.zhyuan.todolist.service;

import com.zhyuan.todolist.exception.ResourceNotFoundException;
import com.zhyuan.todolist.model.*;
import com.zhyuan.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // 标记这个类是一个Spring服务组件，Spring会自动管理它的生命周期
@RequiredArgsConstructor // Lombok: 自动生成一个包含所有final字段的构造函数，用于注入依赖
@Transactional(readOnly = true) // 声明所有方法默认只读事务，对写操作单独配置
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    /**
     * 创建一个新的待办事项
     * @param request 包含待办事项标题和描述的请求体
     * @return 创建成功的待办事项响应DTO
     */
    @Transactional // 对写操作明确声明事务
    public TodoResponse createTodo(TodoCreateRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(false); // 新建的待办事项默认未完成
        // createdAt和updatedAt由JPA自动处理
        if (request.getPriority() != null) { // 允许请求中不带priority，使用默认值
            todo.setPriority(request.getPriority());
        }

        Todo savedTodo = todoRepository.save(todo);     //保存到数据库
        return new TodoResponse(savedTodo);             //返回响应DTO
    }

    /**
     * 获取所有待办事项列表，支持搜索和完成状态过滤，并按更新时间降序、创建时间降序排序
     * @param search 可选的搜索关键字
     * @param completed 可选的完成状态过滤
     * @return 待办事项的响应DTO列表
     */
    public List<TodoResponse> getAllTodos(String search, Boolean completed, String sortBy) {
        List<Todo> todos;
        String searchKeyword = (search != null && !search.trim().isEmpty()) ? search : null;

        if ("priority".equalsIgnoreCase(sortBy)) {
            // 当按优先级排序时，调用新的、专门的方法
            todos = todoRepository.searchWithPrioritySort(searchKeyword, completed);
        } else {
            // 对于其他排序方式，保持原有逻辑
            Sort sort;
            if ("createdAt".equalsIgnoreCase(sortBy)) {
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
            } else {
                // 默认按 updatedAt 降序
                sort = Sort.by(Sort.Direction.DESC, "updatedAt");
            }

            if (searchKeyword != null) {
                todos = todoRepository.searchTodos(searchKeyword, completed, sort);
            } else if (completed != null) {
                todos = todoRepository.findByCompleted(completed, sort);
            } else {
                todos = todoRepository.findAll(sort);
            }
        }

        return todos.stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }


    /**
     * 更新待办事项的完成状态
     * @param id 待办事项的唯一标识符
     * @param request 包含新的完成状态的请求体
     * @return 更新后的待办事项响应DTO
     * @throws ResourceNotFoundException 如果找不到对应ID的待办事项
     */
    @Transactional // 对写操作明确声明事务
    public TodoResponse updateTodoStatus(Long id, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id " + id));

        todo.setCompleted(request.getCompleted()); // 更新完成状态
        // updatedAt会自动更新

        Todo updatedTodo = todoRepository.save(todo); // 保存更新
        return new TodoResponse(updatedTodo); // 转换为DTO并返回
    }

    /**
     * 完整的编辑操作
     * @param id
     * @param request
     * @return
     */
    @Transactional
    public TodoResponse fullUpdateTodo(Long id, TodoFullUpdateRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id " + id));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        // updatedAt 会自动更新

        Todo updatedTodo = todoRepository.save(todo);
        return new TodoResponse(updatedTodo);
    }

    /**
     * 删除待办事项
     * @param id 待办事项的唯一标识符
     * @throws ResourceNotFoundException 如果找不到对应ID的待办事项
     */
    @Transactional // 对写操作明确声明事务
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) { // 检查是否存在，避免删除不存在的资源时抛出异常
            throw new ResourceNotFoundException("Todo not found with id " + id);
        }
        todoRepository.deleteById(id); // 根据ID删除
    }

}
