package com.example.volunteer_system.model.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserVO {
    private Integer id;
    private String username;
    private String email;
    private LocalDate created_at;
    private Integer role;
    private Integer status;
}
