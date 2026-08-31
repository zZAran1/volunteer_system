package com.example.volunteer_system.exception;

public class RegistrationException extends BaseBusinessException {
    public RegistrationException(String msg) {
        super(1005,msg);
    }
}
