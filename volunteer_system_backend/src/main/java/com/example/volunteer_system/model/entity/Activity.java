package com.example.volunteer_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("activity")
public class Activity {
    @TableId
    private Integer id;
    private Integer poster_id;
    private String title;
    private String address;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
    private Integer headcount;
    private Integer headcount_limit;
    private Integer status;
}
