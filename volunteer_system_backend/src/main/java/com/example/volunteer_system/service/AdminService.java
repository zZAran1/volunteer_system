package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.UserVO;

import java.util.List;

public interface AdminService extends IService<Users> {
    void banUser(String email);
    void unbanUser(String email);
    List<UserVO> getUser();
    List<UserVO> getAllUser();
    void changeRole(String email,int value);
}
