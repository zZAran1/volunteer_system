package com.example.volunteer_system.controller;

import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.RegistrationDTO;
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
    @GetMapping("/underReviewActivity")//管理员看到的待审核中的活动
    public Result<List<ActivityVO>> getUnderReviewActivity() {
        List<ActivityVO> list=activityService.underReviewActivity();
        return Result.success(list);
    }
    @GetMapping("/viewActivity")//用户看到的所有活动（已审核）
    public Result<List<ActivityVO>> viewActivities() {
        List<ActivityVO> list=activityService.viewActivities();
        return Result.success(list);
    }
    @GetMapping("/ongoingActivity")//管理员看到的进行中的活动
    public Result<List<ActivityVO>> getOngoingActivity() {
        List<ActivityVO> list=activityService.ongoingActivity();
        return Result.success(list);
    }
    @GetMapping("/fullActivity")//管理员看到的已满人的活动
    public Result<List<ActivityVO>> getFullActivity() {
        List<ActivityVO> list=activityService.fullActivity();
        return Result.success(list);
    }
    @GetMapping("/endedActivity")//管理员看到的已结束的活动
    public Result<List<ActivityVO>> getEndedActivity() {
        List<ActivityVO> list=activityService.endedActivity();
        return Result.success(list);
    }
    @GetMapping("/rejectedActivity")//管理员看到的审核不通过的活动
    public Result<List<ActivityVO>> getRejectedActivity() {
        List<ActivityVO> list=activityService.rejectedActivity();
        return Result.success(list);
    }
    @DeleteMapping("/deleteMyActivity")
    public Result<Void> deleteMyActivity(@RequestParam Integer activity_id) {
        activityService.deleteMyActivity(activity_id);
        return Result.success("删除该活动成功");
    }
    @DeleteMapping("/deleteActivity")
    public Result<Void> deleteActivity(@RequestParam Integer activity_id) {
        activityService.deleteActivity(activity_id);
        return Result.success("删除该活动成功");
    }
    @PutMapping("/reviewEvent_Approved")
    public Result<Void> reviewEventApproved(@Validated @RequestBody RegistrationDTO dto) {
        activityService.reviewEvent_Approved(dto.getActivity_id());
        return Result.success("审核成功");
    }
    @PutMapping("/reviewEvent_Rejected")
    public Result<Void> reviewEventRejected(@Validated @RequestBody RegistrationDTO dto) {
        activityService.reviewEvent_Rejected(dto.getActivity_id());
        return Result.success("审核成功");
    }
}
