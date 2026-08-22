package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.exception.LoginException;
import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.service.UserService;
import com.example.volunteer_system.util.BCryptPasswordUtil;
import com.example.volunteer_system.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService  {
    BCryptPasswordUtil bCryptPasswordUtil = new BCryptPasswordUtil();
    JwtUtil jwtUtil = new JwtUtil();
    @Override
    public void registerUser(RegisterDTO dto){
        Users user = new Users();
        user.setUsername(dto.getUsername());
        String hash_password = bCryptPasswordUtil.hashPassword(dto.getPassword());
        user.setPassword(hash_password);
        user.setEmail(dto.getEmail());
        this.save(user);
    }
    @Override
    public String login(LoginDTO dto){
        Users db_user=this.lambdaQuery()
                .eq(Users::getEmail,dto.getEmail())
                .one();
        if(bCryptPasswordUtil.checkPassword(dto.getPassword(),db_user.getPassword())){
            return jwtUtil.generateToken(String.valueOf(db_user.getId()));
        }else{
            throw new LoginException("邮箱或者密码错误");
        }
    }
}
