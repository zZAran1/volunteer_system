package com.example.volunteer_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("personal")
public class Personal {
    @TableId
    private Integer id;
    private Integer user_id;
    private String realName;
    private String email;
    private String phone;
    private Integer age;
    private String gender;
    private String idNumber;
    private String current_address;
}
