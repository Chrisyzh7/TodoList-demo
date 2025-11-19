package com.zhyuan.todolist.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoFullUpdateRequest {
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "优先级不能为空")
    private Priority priority;
}
