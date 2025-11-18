package com.zhyuan.todolist.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 255, message = "标题长度必须在1到255个字符之间")
    private String title;

    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;


}
