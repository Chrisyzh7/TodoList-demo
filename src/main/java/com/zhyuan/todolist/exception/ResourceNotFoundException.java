package com.zhyuan.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus 注解会使Spring Boot在抛出此异常时自动返回指定的HTTP状态码
@ResponseStatus(HttpStatus.NOT_FOUND) // 当资源未找到时，返回404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message); // 调用父类构造函数，传递错误信息
    }
}
