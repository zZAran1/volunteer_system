package com.example.volunteer_system.task;

import com.example.volunteer_system.mapper.ActivityMapper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActivityStatusTask {
    private final ActivityMapper activityMapper;
    public ActivityStatusTask(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshActivityStatus() {
        activityMapper.refreshEnded();
        activityMapper.refreshOngoing();
        activityMapper.refreshFull();
        activityMapper.refreshUnFull();
    }
}
