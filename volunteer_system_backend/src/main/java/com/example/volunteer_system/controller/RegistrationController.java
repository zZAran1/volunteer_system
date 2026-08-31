package com.example.volunteer_system.controller;

import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.result.Result;
import com.example.volunteer_system.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
@Validated
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/registrant")
    public Result<Void> registrant(@Validated RegistrationDTO dto) {
        registrationService.registrant(dto);
        return Result.success("报名成功");
    }

    @PostMapping("/unRegistrant")
    public Result<Void> unRegistrant(@Validated RegistrationDTO dto) {
        registrationService.unRegistrant(dto);
        return Result.success("取消报名成功");
    }
}
