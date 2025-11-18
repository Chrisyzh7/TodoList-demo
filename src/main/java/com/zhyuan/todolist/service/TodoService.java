package com.zhyuan.todolist.service;

import com.zhyuan.todolist.model.TodoCreateRequest;
import com.zhyuan.todolist.model.TodoResponse;
import com.zhyuan.todolist.model.TodoUpdateRequest;

import java.util.List;

public interface TodoService {
    TodoResponse createTodo(TodoCreateRequest request);

    List<TodoResponse> getAllTodos();

    TodoResponse getTodoById(Long id);

    TodoResponse updateTodoStatus(Long id, TodoUpdateRequest request);

    void deleteTodo(Long id);
}
