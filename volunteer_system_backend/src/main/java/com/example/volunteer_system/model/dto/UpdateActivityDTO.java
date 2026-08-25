package com.example.volunteer_system.model.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UpdateActivityDTO {
    private int id;
    private String title;
    private String address;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
    private Integer headcount_limit;
}
