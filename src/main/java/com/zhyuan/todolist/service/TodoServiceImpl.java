package com.zhyuan.todolist.service;

import com.zhyuan.todolist.exception.ResourceNotFoundException;
import com.zhyuan.todolist.model.Todo;
import com.zhyuan.todolist.model.TodoCreateRequest;
import com.zhyuan.todolist.model.TodoResponse;
import com.zhyuan.todolist.model.TodoUpdateRequest;
import com.zhyuan.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        Todo savedTodo = todoRepository.save(todo);     //保存到数据库
        return new TodoResponse(savedTodo);             //返回响应DTO
    }

    /**
     * 获取所有待办事项列表
     * @return 所有待办事项的响应DTO列表
     */
    public List<TodoResponse> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream()
                .map(TodoResponse::new)
                .toList();
    }

    /**
     * 根据ID获取单个待办事项
     * @param id 待办事项的唯一标识符
     * @return 对应的待办事项响应DTO
     * @throws ResourceNotFoundException 如果找不到对应ID的待办事项
     */
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id " + id));
        return new TodoResponse(todo);
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
