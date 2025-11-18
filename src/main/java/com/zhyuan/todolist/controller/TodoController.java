package com.zhyuan.todolist.controller;

import com.zhyuan.todolist.model.TodoCreateRequest;
import com.zhyuan.todolist.model.TodoResponse;
import com.zhyuan.todolist.model.TodoUpdateRequest;
import com.zhyuan.todolist.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 声明这是一个RESTful控制器，返回JSON/XML数据
@RequestMapping("/api/todos") // 设定所有处理方法的基础URL路径
@RequiredArgsConstructor // Lombok: 自动生成一个包含所有final字段的构造函数，用于注入依赖
// @CrossOrigin(origins = "http://localhost:port") // 如果你的前端运行在不同的端口，这里可以配置CORS
public class TodoController {

    private final TodoService todoService; // 注入TodoService

    /**
     * 创建一个新的待办事项
     * POST /api/todos
     * @param request 包含待办事项标题和描述的请求体
     * @return 创建成功的待办事项响应DTO和HTTP 201 Created状态码
     */
    @PostMapping // 处理POST请求
    public ResponseEntity<TodoResponse> createTodo(@Validated @RequestBody TodoCreateRequest request) {
        // @Validated 激活对请求体 (request) 中的 JSR 303/349/380 注解 (如@NotBlank, @Size) 的校验
        // @RequestBody 表示将HTTP请求体的内容反序列化为TodoCreateRequest对象
        TodoResponse newTodo = todoService.createTodo(request);
        return new ResponseEntity<>(newTodo, HttpStatus.CREATED); // 返回201 Created状态码
    }

    /**
     * 获取所有待办事项列表
     * GET /api/todos
     * @return 所有待办事项的响应DTO列表和HTTP 200 OK状态码
     */
    @GetMapping // 处理GET请求
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos); // 返回200 OK状态码
    }

    /**
     * 根据ID获取单个待办事项
     * GET /api/todos/{id}
     * @param id 待办事项的唯一标识符
     * @return 对应的待办事项响应DTO和HTTP 200 OK状态码
     */
    @GetMapping("/{id}") // 处理GET请求，并从URL路径中提取ID
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        // @PathVariable 表示从URL路径变量中获取ID
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo); // 返回200 OK状态码
    }

    /**
     * 更新待办事项的完成状态
     * PATCH /api/todos/{id}
     * @param id 待办事项的唯一标识符
     * @param request 包含新的完成状态的请求体
     * @return 更新后的待办事项响应DTO和HTTP 200 OK状态码
     */
    @PatchMapping("/{id}") // 处理PATCH请求，用于部分更新资源
    public ResponseEntity<TodoResponse> updateTodoStatus(
            @PathVariable Long id,
            @Validated @RequestBody TodoUpdateRequest request) {
        TodoResponse updatedTodo = todoService.updateTodoStatus(id, request);
        return ResponseEntity.ok(updatedTodo); // 返回200 OK状态码
    }

    /**
     * 删除待办事项
     * DELETE /api/todos/{id}
     * @param id 待办事项的唯一标识符
     * @return HTTP 204 No Content状态码
     */
    @DeleteMapping("/{id}") // 处理DELETE请求
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build(); // 返回204 No Content状态码，表示成功处理但没有返回内容
    }
}
