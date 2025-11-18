package com.zhyuan.todolist.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TodoUpdateRequest {
    //用于更新待办事项，特别是完成状态

    //@NotBlank(message = "完成状态不能为空")
    @NotNull
    private Boolean completed;      // 待办事项的完成状态
}
