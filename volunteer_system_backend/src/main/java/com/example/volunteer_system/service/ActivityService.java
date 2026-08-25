package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;

public interface ActivityService extends IService<Activity> {
    void createActivity(CreateActivityDTO dto);
    void updateActivity(UpdateActivityDTO dto);
    ActivityVO getActivity();
    void deleteActivity();
}
