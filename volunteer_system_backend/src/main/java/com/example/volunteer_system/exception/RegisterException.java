package com.example.volunteer_system.exception;

public class RegisterException extends BaseBusinessException{
    public RegisterException(String msg) {
        super(1000,msg);
    }
}
