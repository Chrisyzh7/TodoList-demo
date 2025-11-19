package com.zhyuan.todolist.repository;

import com.zhyuan.todolist.model.Todo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * 场景 1: 只有【状态筛选】，使用动态排序
     * (例如: 显示所有"未完成"任务，按创建时间排序)
     */
    List<Todo> findByCompleted(Boolean completed, Sort sort);

    /**
     * 场景 2: 有【搜索关键字】(同时支持状态筛选)，使用动态排序
     * (例如: 搜索"会议"，且只看"未完成"的，按更新时间排序)
     * 注: :completed IS NULL 的判断让这个方法非常灵活
     */
    @Query("SELECT t FROM Todo t WHERE " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))" +
            "AND (:completed IS NULL OR t.completed = :completed)")
    List<Todo> searchTodos(@Param("searchKeyword") String searchKeyword,
                           @Param("completed") Boolean completed,
                           Sort sort);

    /**
     * 场景 3: 【按优先级排序】的专用查询
     * (因为优先级的 HIGH/MEDIUM/LOW 需要自定义逻辑顺序，普通 Sort 对象无法处理)
     */
    @Query("SELECT t FROM Todo t WHERE " +
            "(:searchKeyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))" +
            "AND (:completed IS NULL OR t.completed = :completed) " +
            "ORDER BY CASE t.priority " +
            "WHEN 'HIGH' THEN 0 " +
            "WHEN 'MEDIUM' THEN 1 " +
            "WHEN 'LOW' THEN 2 " +
            "ELSE 3 END, t.updatedAt DESC")
    List<Todo> searchWithPrioritySort(@Param("searchKeyword") String searchKeyword,
                                      @Param("completed") Boolean completed);
}