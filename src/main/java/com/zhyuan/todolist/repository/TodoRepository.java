package com.zhyuan.todolist.repository;

import com.zhyuan.todolist.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo,Long> {
    // Spring Data JPA 会自动为我们生成实现，提供以下基本CRUD方法：
    // save(Todo entity): 保存一个Todo (新增或更新)
    // findById(Long id): 根据ID查找Todo
    // findAll(): 查找所有Todo
    // deleteById(Long id): 根据ID删除Todo
    // delete(Todo entity): 删除一个Todo
    // existsById(Long id): 检查是否存在指定ID的Todo

    // 此外，我们还可以根据命名约定定义自定义查询方法，例如：
    List<Todo> findByCompleted(Boolean completed); // 根据完成状态查找

    List<Todo> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descriptionKeyword); // 用于搜索功能

    // 4. (新增) 查找所有待办事项，并按updatedAt降序、createdAt降序排序
    // Spring Data JPA 会自动识别 OrderByUpdated_AtDescAndCreated_AtDesc
    List<Todo> findAllByOrderByUpdatedAtDescCreatedAtDesc();

    // 5. (新增) 结合搜索/completed过滤后的排序
    // @Query 注解也可以添加 ORDER BY 子句
    @Query("SELECT t FROM Todo t WHERE " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))" +
            "AND (:completed IS NULL OR t.completed = :completed) " +
            "ORDER BY t.updatedAt DESC, t.createdAt DESC") // 添加排序规则
    List<Todo> searchTodosWithSorting(@Param("searchKeyword") String searchKeyword, @Param("completed") Boolean completed);

    // 6. (新增) 查找指定完成状态的任务，并按updatedAt降序、createdAt降序排序
    List<Todo> findByCompletedOrderByUpdatedAtDescCreatedAtDesc(Boolean completed);
}
