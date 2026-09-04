package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    void createActivity(CreateActivityDTO dto);
    void updateActivity(UpdateActivityDTO dto);
    List<ActivityVO> getAllActivities();
    List<ActivityVO> viewActivities();
    List<ActivityVO> getMyActivities();
    List<ActivityVO> getRegistered();
    List<ActivityVO> underReviewActivity();
    List<ActivityVO> endedActivity();
    List<ActivityVO> fullActivity();
    List<ActivityVO> ongoingActivity();
    List<ActivityVO> rejectedActivity();
    void deleteMyActivity(UpdateActivityDTO dto);
    void deleteActivity(RegistrationDTO dto);
    void reviewEvent_Approved(int activityId);
    void reviewEvent_Rejected(int activityId);
}
