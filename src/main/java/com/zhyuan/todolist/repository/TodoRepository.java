package com.zhyuan.todolist.repository;

import com.zhyuan.todolist.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo,Long> {
    // Spring Data JPA 会自动为我们生成实现，提供以下基本CRUD方法：
    // save(Todo entity): 保存一个Todo (新增或更新)
    // findById(Long id): 根据ID查找Todo
    // findAll(): 查找所有Todo
    // deleteById(Long id): 根据ID删除Todo
    // delete(Todo entity): 删除一个Todo
    // existsById(Long id): 检查是否存在指定ID的Todo

    // 此外，我们还可以根据命名约定定义自定义查询方法，例如：
    // List<Todo> findByCompleted(Boolean completed); // 根据完成状态查找
    // List<Todo> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descriptionKeyword); // 用于搜索功能
}
