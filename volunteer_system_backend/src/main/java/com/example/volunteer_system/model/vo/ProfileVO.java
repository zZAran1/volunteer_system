package com.example.volunteer_system.model.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ProfileVO {
    private String username;
    private String email;
    private LocalDate created_at;
    private Integer role;
    private String image_url;
}
