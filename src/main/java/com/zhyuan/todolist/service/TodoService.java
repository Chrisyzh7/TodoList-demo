package com.zhyuan.todolist.service;

import com.zhyuan.todolist.model.TodoCreateRequest;
import com.zhyuan.todolist.model.TodoFullUpdateRequest;
import com.zhyuan.todolist.model.TodoResponse;
import com.zhyuan.todolist.model.TodoUpdateRequest;

import java.util.List;

public interface TodoService {
    TodoResponse createTodo(TodoCreateRequest request);

    List<TodoResponse> getAllTodos(String search, Boolean completed,String sortBy);



    TodoResponse updateTodoStatus(Long id, TodoUpdateRequest request);

    TodoResponse fullUpdateTodo(Long id, TodoFullUpdateRequest request);

    void deleteTodo(Long id);
}
