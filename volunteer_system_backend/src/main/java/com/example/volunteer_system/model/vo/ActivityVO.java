package com.example.volunteer_system.model.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ActivityVO {
    private String poster_name;
    private String title;
    private String address;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
    private Integer headcount;
    private Integer headcount_limit;
    private Integer status;
}
