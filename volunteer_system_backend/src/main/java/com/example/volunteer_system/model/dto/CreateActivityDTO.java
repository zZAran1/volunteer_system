package com.example.volunteer_system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class CreateActivityDTO {
    @NotBlank(message = "活动标题不能为空")
    private String title;
    @NotBlank(message = "活动地址不能为空")
    private String address;
    @NotNull(message = "开始时间不能为空")
    private LocalDate start_date;
    @NotNull(message = "结束时间不能为空")
    private LocalDate end_date;
    @NotBlank(message = "活动描述不能为空")
    private String description;
    @NotNull(message = "报名人数上限不能为空")
    private Integer headcount_limit;
}
