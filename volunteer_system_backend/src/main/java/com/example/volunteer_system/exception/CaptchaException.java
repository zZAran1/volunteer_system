package com.example.volunteer_system.exception;

public class CaptchaException extends BaseBusinessException {
    public CaptchaException(String msg) {
        super(1003,msg);
    }
}
