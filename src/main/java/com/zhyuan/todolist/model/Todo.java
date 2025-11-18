package com.zhyuan.todolist.model;

import jakarta.persistence.*; // JPA 标准注解
import lombok.Data; // Lombok 注解，自动生成 getter/setter/equals/hashCode/toString
import lombok.NoArgsConstructor; // Lombok 注解，生成无参构造函数
import org.hibernate.annotations.CreationTimestamp; // Hibernate 扩展，用于自动生成创建时间
import org.hibernate.annotations.UpdateTimestamp; // Hibernate 扩展，用于自动生成更新时间

import java.time.LocalDateTime; // 使用 Java 8+ 的日期时间API

@Entity // 声明这是一个JPA实体，对应数据库中的一张表
@Table(name = "todos") // 指定数据库表名为 "todos"
@Data // Lombok: 自动生成所有字段的 getter/setter, equals, hashCode, toString 方法
@NoArgsConstructor // Lombok: 自动生成一个无参构造函数 (JPA 规范要求)
public class Todo {

    @Id // 声明这是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略：数据库自增
    private Long id; // 待办事项唯一标识符

    @Column(nullable = false) // 声明该字段映射到数据库列，且不允许为空
    private String title; // 标题 (必填)

    @Column(length = 1000) // 指定最大长度
    private String description; // 描述 (可选，可为空)

    @Column(nullable = false) // 不允许为空
    private Boolean completed = false; // 完成状态 (默认未完成)

    @CreationTimestamp // 自动在实体创建时设置当前时间
    @Column(nullable = false, updatable = false) // 不可为空，且创建后不可更新
    private LocalDateTime createdAt; // 创建时间

    @UpdateTimestamp // 自动在实体更新时设置当前时间
    @Column(nullable = false) // 不可为空
    private LocalDateTime updatedAt; // 更新时间


}
