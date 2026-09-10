package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.ActivityException;
import com.example.volunteer_system.exception.RegistrationException;
import com.example.volunteer_system.exception.ReviewEventException;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.ActivityMapper;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.SelectActivityDTO;
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
    private void updateTime(){
        this.baseMapper.refreshEnded();
        this.baseMapper.refreshOngoing();
        this.baseMapper.refreshFull();
        this.baseMapper.refreshUnFull();
    }
    private void checkActivityStatus(int activityId){
        Activity db_activity = this.lambdaQuery()
                .eq(Activity::getId,activityId)
                .select(Activity::getStatus)
                .one();
        if(db_activity.getStatus()!=0){
            throw new ReviewEventException("审核失败");
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
        Activity db_activity=this.lambdaQuery()
                .eq(Activity::getId ,activity.getId())
                .select(Activity::getStatus,Activity::getHeadcount,Activity::getStart_date)
                .one();
        switch (db_activity.getStatus()){
            case 2:
                throw new ActivityException("无法修改进行中的活动");
            case 4:
                throw new ActivityException("无法修改已结束的活动");
            case 5:
                throw new ActivityException("无法修改审核未通过的活动");
        }
        if(db_activity.getHeadcount()>activity.getHeadcount_limit()){
            throw new ActivityException("现已报名的人数比修改后的人数上限多，请重新编辑修改信息");
        }
        this.lambdaUpdate()
                .eq(Activity::getId, activity.getId())
                .eq(Activity::getPoster_id, userId)   // 只能修改自己发布的活动
                .set(Activity::getStatus,0)
                .update(activity);
    }
    @Override
    public List<ActivityVO> selectActivity(SelectActivityDTO dto){
        updateTime();
        return this.baseMapper.titleSelectActivity(dto.getTitle(),1);
    }
    @Override
    public List<ActivityVO> getAllActivities() {
        checkRole();
        updateTime();
        return this.baseMapper.adminSelectAllActivity();
    }
    @Override
    public List<ActivityVO> getMyActivities() {
        int userId = UserContext.getUserId();
        updateTime();
        return this.baseMapper.selectMyActivity(userId);
    }
    @Override
    public List<ActivityVO> getRegistered(){
        int userId = UserContext.getUserId();
        updateTime();
        return this.baseMapper.selectRegistered(userId);
    }
    @Override
    public List<ActivityVO> underReviewActivity(){
        checkRole();
        updateTime();
        return this.baseMapper.SelectActivity(0);
    }
    @Override
    public List<ActivityVO> viewActivities(){
        updateTime();
        return this.baseMapper.SelectActivity(1);
    }
    @Override
    public List<ActivityVO> ongoingActivity(){
        checkRole();
        updateTime();
        return this.baseMapper.SelectActivity(2);
    }
    @Override
    public List<ActivityVO> fullActivity(){
        checkRole();
        updateTime();
        return this.baseMapper.SelectActivity(3);
    }
    @Override
    public List<ActivityVO> endedActivity(){
        checkRole();
        updateTime();
        return this.baseMapper.SelectActivity(4);
    }
    @Override
    public List<ActivityVO> rejectedActivity(){
        checkRole();
        updateTime();
        return this.baseMapper.SelectActivity(5);
    }
    @Override
    public void deleteMyActivity(Integer id) {
        int userId = UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Activity::getId,id)
                .eq(Activity::getPoster_id, userId)// 只能删除自己发布的活动
                .remove();
    }
    @Override
    public void deleteActivity(Integer activity_id){
        checkRole();
        this.lambdaUpdate()
                .eq(Activity::getId,activity_id)
                .remove();
    }
    @Override
    public void reviewEvent_Approved(int activityId){
        checkRole();
        checkActivityStatus(activityId);
        this.lambdaUpdate()
                .eq(Activity::getId,activityId)
                .set(Activity::getStatus,1)
                .update();
    }
    @Override
    public void reviewEvent_Rejected(int activityId){
        checkRole();
        checkActivityStatus(activityId);
        this.lambdaUpdate()
                .eq(Activity::getId,activityId)
                .set(Activity::getStatus,5)
                .update();
    }
}