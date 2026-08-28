package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateProfileDTO;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.ProfileVO;
import org.springframework.web.multipart.MultipartFile;


public interface UserService extends IService<Users> {
    void register(RegisterDTO dto);
    String login(LoginDTO dto);
    void updateProfile(UpdateProfileDTO dto);
    void avatarUpdate(MultipartFile file);
    ProfileVO getProfile();
    void deleteUser();
}
