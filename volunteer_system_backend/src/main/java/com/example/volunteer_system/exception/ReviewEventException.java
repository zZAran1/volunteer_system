package com.example.volunteer_system.exception;

public class ReviewEventException extends BaseBusinessException{
    public ReviewEventException(String msg) {
        super(1006,msg);
    }
}
