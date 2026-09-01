package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.ActivityMapper;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import com.example.volunteer_system.service.ActivityService;
import com.example.volunteer_system.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    private void checkRole(){
        int role = UserContext.getRole();
        if(role!=0&&role!=1){
            throw new TokenException("该账号权限不足");
        }
    }
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
        this.lambdaUpdate()
                .eq(Activity::getId, id)
                .eq(Activity::getPoster_id, userId)   // 只能修改自己发布的活动
                .set(Activity::getStatus,0)
                .update(activity);
    }
    @Override
    public List<ActivityVO> getAllActivities() {
        checkRole();
        return this.baseMapper.adminSelectAllActivity();
    }
    @Override
    public List<ActivityVO> getMyActivities() {
        int userId = UserContext.getUserId();
        return this.baseMapper.selectMyActivity(userId);
    }
    @Override
    public List<ActivityVO> getRegistered(){
        int userId = UserContext.getUserId();
        return this.baseMapper.selectRegistered(userId);
    }
    @Override
    public List<ActivityVO> underReviewActivity(){
        checkRole();
        return this.baseMapper.SelectActivity(0);
    }
    @Override
    public List<ActivityVO> viewActivities(){
        return this.baseMapper.SelectActivity(1);
    }
    @Override
    public List<ActivityVO> ongoingActivity(){
        checkRole();
        return this.baseMapper.SelectActivity(2);
    }
    @Override
    public List<ActivityVO> fullActivity(){
        checkRole();
        return this.baseMapper.SelectActivity(3);
    }
    @Override
    public List<ActivityVO> endedActivity(){
        checkRole();
        return this.baseMapper.SelectActivity(4);
    }
    @Override
    public List<ActivityVO> rejectedActivity(){
        checkRole();
        return this.baseMapper.SelectActivity(5);
    }
    @Override
    public void deleteMyActivity(UpdateActivityDTO dto) {
        int userId = UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Activity::getId, dto.getId())
                .eq(Activity::getPoster_id, userId)// 只能删除自己发布的活动
                .remove();
    }
    @Override
    public void deleteActivity(RegistrationDTO dto){
        checkRole();
        this.lambdaUpdate()
                .eq(Activity::getId,dto.getActivity_id())
                .remove();
    }
    @Override
    public void reviewEvent_Approved(int activityId){
        checkRole();
        this.lambdaUpdate()
                .eq(Activity::getId,activityId)
                .set(Activity::getStatus,1)
                .update();
    }
    @Override
    public void reviewEvent_Rejected(int activityId){
        checkRole();
        this.lambdaUpdate()
                .eq(Activity::getId,activityId)
                .set(Activity::getStatus,5)
                .update();
    }
}