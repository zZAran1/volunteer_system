package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.exception.RegistrationException;
import com.example.volunteer_system.mapper.RegistrationMapper;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.entity.Registration;
import com.example.volunteer_system.service.ActivityService;
import com.example.volunteer_system.service.RegistrationService;
import com.example.volunteer_system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {
    private final ActivityService activityService;
    @Override
    @Transactional
    public void registrant(RegistrationDTO dto){
        int user_id =UserContext.getUserId();
        Registration registration = new Registration();
        Activity activity = activityService.getById(dto.getActivity_id());
        if(activity!=null && activity.getHeadcount_limit() != null && activity.getHeadcount() >= activity.getHeadcount_limit()){
            throw new RegistrationException("该活动报名人数已满");
        }
        registration.setRegistrant_id(user_id);
        registration.setActivity_id(dto.getActivity_id());
        this.save(registration);
        activityService.lambdaUpdate()
                .eq(Activity::getId, dto.getActivity_id())
                .setSql("headcount=headcount + 1")
                .update();
    }
    @Override
    @Transactional
    public void unRegistrant(RegistrationDTO dto){
        int user_id =UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Registration::getRegistrant_id, user_id)
                .eq(Registration::getActivity_id, dto.getActivity_id())
                .remove();
        activityService.lambdaUpdate()
                .eq(Activity::getId, dto.getActivity_id())
                .gt(Activity::getHeadcount, 0)
                .setSql("headcount=headcount - 1")
                .update();
    }
}
