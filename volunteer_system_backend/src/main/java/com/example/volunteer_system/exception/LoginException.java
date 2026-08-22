package com.example.volunteer_system.exception;

public class LoginException extends BaseBusinessException{
    public LoginException(String msg) {
        super(1001,msg);
    }
}
