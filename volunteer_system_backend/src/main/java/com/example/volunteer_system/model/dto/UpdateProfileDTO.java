package com.example.volunteer_system.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String username;
}
