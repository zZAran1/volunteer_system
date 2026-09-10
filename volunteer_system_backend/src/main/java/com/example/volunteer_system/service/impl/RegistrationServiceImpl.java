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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {
    @Autowired
    private  ActivityService activityService;
    @Override
    @Transactional
    public void registrant(int activity_id){
        int user_id =UserContext.getUserId();
        Activity db_activity=activityService.lambdaQuery()
                .eq(Activity::getId,activity_id)
                .select(Activity::getPoster_id,Activity::getStatus)
                .one();
        if(db_activity==null||db_activity.getStatus()!=1){
            throw new RegistrationException("活动报名失败");
        }
        if(db_activity.getPoster_id()==user_id){
            throw new RegistrationException("不能报名自己发布的活动");
        }
        if (!this.baseMapper.insertRegistration(activity_id,user_id)) {
            throw new RegistrationException("请勿重复报名");
        }
        boolean updated = activityService.lambdaUpdate()
                .eq(Activity::getId, activity_id)
                .apply("headcount < headcount_limit")
                .setSql("headcount = headcount + 1")
                .update();
        if (!updated) {
            throw new RegistrationException("该活动报名人数已满");
        }
    }
    @Override
    @Transactional
    public void unRegistrant(int activity_id){
        int user_id =UserContext.getUserId();
        boolean deleted =this.lambdaUpdate()
                .eq(Registration::getRegistrant_id, user_id)
                .eq(Registration::getActivity_id, activity_id)
                .remove();
        if(deleted){
            boolean update=activityService.lambdaUpdate()
                    .eq(Activity::getId, activity_id)
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
