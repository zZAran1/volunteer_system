package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.entity.Registration;

public interface RegistrationService extends IService<Registration> {
    void registrant(int activity_id);
    void unRegistrant(int activity_id);
}
