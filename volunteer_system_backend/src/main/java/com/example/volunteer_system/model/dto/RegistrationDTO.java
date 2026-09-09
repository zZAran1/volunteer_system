package com.example.volunteer_system.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotNull(message ="活动id不能为空")
    private Integer activity_id;
}
