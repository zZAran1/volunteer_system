package com.example.volunteer_system.controller;

import com.example.volunteer_system.service.PersonalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
@Validated
public class PersonalController {
    private final PersonalService personalService;


}
