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
        if (!this.baseMapper.insertRegistration(dto.getActivity_id(),user_id)) {
            throw new RegistrationException("请勿重复报名");
        }
        boolean updated = activityService.lambdaUpdate()
                .eq(Activity::getId, dto.getActivity_id())
                .apply("headcount < headcount_limit")
                .setSql("headcount = headcount + 1")
                .update();
        if (!updated) {
            throw new RegistrationException("该活动报名人数已满");
        }
    }
    @Override
    @Transactional
    public void unRegistrant(RegistrationDTO dto){
        int user_id =UserContext.getUserId();
        boolean deleted =this.lambdaUpdate()
                .eq(Registration::getRegistrant_id, user_id)
                .eq(Registration::getActivity_id, dto.getActivity_id())
                .remove();
        if(deleted){
            boolean update=activityService.lambdaUpdate()
                    .eq(Activity::getId, dto.getActivity_id())
                    .gt(Activity::getHeadcount, 0)
                    .setSql("headcount=headcount - 1")
                    .update();
            if(!update){
                throw new RegistrationException("系统异常，取消失败");
            }
        }else{
            throw new RegistrationException("您未报名该活动或已取消报名");
        }
    }
}
