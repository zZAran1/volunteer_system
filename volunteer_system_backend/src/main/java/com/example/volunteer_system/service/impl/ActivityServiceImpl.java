package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.mapper.ActivityMapper;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import com.example.volunteer_system.service.ActivityService;
import com.example.volunteer_system.util.UserContext;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper,Activity> implements ActivityService  {
    @Override
    public void createActivity(CreateActivityDTO dto){
        int user_id= UserContext.getUserId();
        Activity activity= Converter.INSTANCE.toActivity(dto);
        activity.setPoster_id(user_id);
        this.save(activity);
    }
    @Override
    public void updateActivity(UpdateActivityDTO dto){
        int user_id= UserContext.getUserId();
        
    }
    @Override
    public ActivityVO getActivity(){

    }
    @Override
    public void deleteActivity(){

    }
}
