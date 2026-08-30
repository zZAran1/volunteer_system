package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.mapper.RegistrationMapper;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.entity.Registration;
import com.example.volunteer_system.service.RegistrationService;
import com.example.volunteer_system.util.UserContext;

public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {
    @Override
    public void registrant(RegistrationDTO dto){
        int user_id =UserContext.getUserId();
        Registration registration = new Registration();
        registration.setRegistrant_id(user_id);
        registration.setActivity_id(dto.getActivity_id());
        this.save(registration);
    }
    @Override
    public void unRegistrant(RegistrationDTO dto){
        int user_id =UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Registration::getRegistrant_id, user_id)
                .remove();
    }
}
