package com.example.volunteer_system.exception;

public class ProfileException extends BaseBusinessException{
    public ProfileException(String msg){
        super(1004,msg);
    }
}
