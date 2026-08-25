package com.example.volunteer_system.exception;

public class TokenException extends BaseBusinessException{
    public TokenException(String msg) {
        super(1002,msg);
    }
}
