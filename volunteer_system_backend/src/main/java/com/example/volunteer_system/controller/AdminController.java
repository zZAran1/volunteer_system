package com.example.volunteer_system.controller;

import com.example.volunteer_system.model.dto.ChangeRoleDTO;
import com.example.volunteer_system.model.dto.UsernameDTO;
import com.example.volunteer_system.model.vo.UserVO;
import com.example.volunteer_system.result.Result;
import com.example.volunteer_system.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final AdminService adminService;
    @PutMapping("/banUser")
    public Result<Void> banUser(@Valid @RequestBody UsernameDTO dto) {
        adminService.banUser(dto.getUsername());
        return Result.success("禁用用户成功");
    }
    @PutMapping("/unbanUser")
    public Result<Void> unbanUser(@Valid @RequestBody UsernameDTO dto) {
        adminService.unbanUser(dto.getUsername());
        return Result.success("解封用户成功");
    }
    @GetMapping("/getUser")
    public Result<List<UserVO>> getUser() {
        List<UserVO> list=adminService.getUser();
        return Result.success(list);
    }
    @GetMapping("/getAllUser")
    public Result<List<UserVO>> getAllUser() {
        List<UserVO> list=adminService.getAllUser();
        return Result.success(list);
    }
    @PostMapping("/changeRole")
    public Result<Void> changeRole(@Valid @RequestBody ChangeRoleDTO dto){
        adminService.changeRole(dto.getUsername(),dto.getValue());
        return Result.success("修改权限成功");
    }
}
