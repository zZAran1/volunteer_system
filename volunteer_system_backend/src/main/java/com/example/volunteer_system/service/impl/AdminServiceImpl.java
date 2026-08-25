package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.UserVO;
import com.example.volunteer_system.service.AdminService;
import com.example.volunteer_system.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper,Users> implements AdminService {
    @Override
    public void banUser(String email){
        if(UserContext.getRole()==2){
            throw new TokenException("该账号没有此权限");
        }
        this.lambdaUpdate()
                .eq(Users::getEmail,email)
                .set(Users::getStatus,2)
                .update();
    }
    @Override
    public void unbanUser(String email){
        if(UserContext.getRole()==2){
            throw new TokenException("该账号没有此权限");
        }
        this.lambdaUpdate()
                .eq(Users::getEmail,email)
                .set(Users::getStatus,1)
                .update();
    }
    @Override
    public List<UserVO> getUser(){
        if(UserContext.getRole()==2){
            throw new TokenException("该账号没有此权限");
        }
        List<Users> db_user=this.lambdaQuery()
                .eq(Users::getRole,2)
                .list();
        return db_user.stream()
                .map(Converter.INSTANCE::toUsersVO)
                .collect(Collectors.toList());
    }
    @Override
    public List<UserVO> getAllUser(){
        if(UserContext.getRole()!=0){
            throw new TokenException("该账号没有此权限");
        }
        List<Users> db_user=this.lambdaQuery()
                .eq(Users::getRole,2)
                .or()
                .eq(Users::getRole,1)
                .list();
        return db_user.stream()
                .map(Converter.INSTANCE::toUsersVO)
                .collect(Collectors.toList());
    }
    @Override
    public void changeRole(String email,int value){
        if(UserContext.getRole()!=0){
            throw new TokenException("该账号没有权限");
        }
        if(value!=1 && value!=2){
            throw new TokenException("修改的权限无效");
        }
        this.lambdaUpdate()
                .eq(Users::getEmail,email)
                .set(Users::getRole,value)
                .update();
    }
}
