package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    @CacheEvict(cacheNames = "activities", allEntries = true)
    void createActivity(CreateActivityDTO dto);
    @CacheEvict(cacheNames = "activities", allEntries = true)
    void updateActivity(UpdateActivityDTO dto);
    @Cacheable(cacheNames = "activities", key = "'admin-all'")
    List<ActivityVO> getAllActivities();
    @Cacheable(cacheNames = "activities", key = "'all'")
    List<ActivityVO> viewActivities();
    List<ActivityVO> getMyActivities();
    List<ActivityVO> getRegistered();
    @CacheEvict(cacheNames = "activities", allEntries = true)
    void deleteActivity(UpdateActivityDTO dto);
}
