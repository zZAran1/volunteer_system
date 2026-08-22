package com.example.volunteer_system.service;

import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;

public interface UserService {
    void registerUser(RegisterDTO dto);
    String login(LoginDTO dto);
}
