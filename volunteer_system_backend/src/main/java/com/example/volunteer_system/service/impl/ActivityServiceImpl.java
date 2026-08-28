package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.ActivityMapper;
import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import com.example.volunteer_system.service.ActivityService;
import com.example.volunteer_system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    private final UserMapper userMapper;

    @Override
    public void createActivity(CreateActivityDTO dto) {
        int userId = UserContext.getUserId();
        Activity activity = Converter.INSTANCE.toActivity(dto);
        activity.setPoster_id(userId);
        activity.setHeadcount(0);
        activity.setStatus(0);
        this.save(activity);
    }

    @Override
    public void updateActivity(UpdateActivityDTO dto) {
        int userId = UserContext.getUserId();
        Activity activity = Converter.INSTANCE.toActivity(dto);
        Integer id = activity.getId();
        activity.setId(null);
        this.lambdaUpdate()
                .eq(Activity::getId, id)
                .eq(Activity::getPoster_id, userId)   // 只能修改自己发布的活动
                .update(activity);
    }
    @Override
    public List<ActivityVO> getAllActivities() {
        if(UserContext.getRole()!=0&&UserContext.getUserId()!=1){
            throw new TokenException("该账号没权限进行该操作");
        }
        return this.baseMapper.adminSelectAllActivity();
    }
    @Override
    public List<ActivityVO> viewActivities(){
        return this.baseMapper.userSelectAllActivity();
    }
    @Override
    public List<ActivityVO> getMyActivities() {
        int userId = UserContext.getUserId();
        return this.baseMapper.selectMyActivity(userId);
    }
    @Override
    public void deleteActivity(UpdateActivityDTO dto) {
        int userId = UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Activity::getId, dto.getId())
                .eq(Activity::getPoster_id, userId)// 只能删除自己发布的活动
                .remove();
    }
}