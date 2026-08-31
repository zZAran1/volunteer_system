package com.example.volunteer_system.controller;

import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.vo.ActivityVO;
import com.example.volunteer_system.result.Result;
import com.example.volunteer_system.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
@Validated
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/create")
    public Result<Void> createActivity(@Validated @RequestBody CreateActivityDTO dto) {
        activityService.createActivity(dto);
        return Result.success("发布志愿活动成功，待管理员审核");
    }
    @PutMapping("/update")
    public Result<Void> updateActivity(@Validated @RequestBody UpdateActivityDTO dto) {
        activityService.updateActivity(dto);
        return Result.success("修改活动内容成功，待管理员审核");
    }
    @GetMapping("/getAllActivity")//管理员看到的所有活动
    public Result<List<ActivityVO>> getAllActivity() {
        List<ActivityVO> list=activityService.getAllActivities();
        return Result.success(list);
    }
    @GetMapping("/viewActivity")//用户看到的所有活动（已审核）
    public Result<List<ActivityVO>> viewActivities() {
        List<ActivityVO> list=activityService.viewActivities();
        return Result.success(list);
    }
    @GetMapping("/getMyActivity")//用户发布的活动
    public Result<List<ActivityVO>> getMyActivity() {
        List<ActivityVO> list=activityService.getMyActivities();
        return Result.success(list);
    }
    @GetMapping("/getRegistered")//用户报名的活动
    public Result<List<ActivityVO>> getRegistered() {
        List<ActivityVO> list=activityService.getRegistered();
        return Result.success(list);
    }
    @DeleteMapping("/delete")
    public Result<Void> deleteActivity(@RequestParam UpdateActivityDTO dto) {
        activityService.deleteActivity(dto);
        return Result.success("删除该活动成功");
    }
}
