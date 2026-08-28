package com.example.volunteer_system.model.vo;

import lombok.Data;

@Data
public class PersonalVO {
    private String realName;
    private String email;
    private String phone;
    private Integer age;
    private String gender;
    private String idNumber;
    private String current_address;
}
