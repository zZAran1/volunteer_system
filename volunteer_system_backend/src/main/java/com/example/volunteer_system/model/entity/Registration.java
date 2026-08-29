package com.example.volunteer_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("registration")
public class Registration {
    private Integer registrant_id;
    private Integer activity_id;
}
