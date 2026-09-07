package com.example.volunteer_system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotNull(message = "角色不能为空")
    private Integer value;
}
