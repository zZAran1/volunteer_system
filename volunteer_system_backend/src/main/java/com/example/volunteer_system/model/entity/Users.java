package com.example.volunteer_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("users")
public class Users {
    @TableId
    private Integer id;
    private String username;
    private String password;
    private String email;
    private LocalDate created_at;
    private Integer role;
    private Integer status;
    private String image_url;
}
