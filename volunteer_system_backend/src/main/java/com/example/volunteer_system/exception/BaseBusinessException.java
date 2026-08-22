package com.example.volunteer_system.exception;

import lombok.Getter;

@Getter
public class BaseBusinessException extends RuntimeException {
    private final int code;
    public BaseBusinessException(int code, String message) {
        super(message); // 直接复用父类的message，不再定义多余的msg字段
        this.code = code;
    }
}
