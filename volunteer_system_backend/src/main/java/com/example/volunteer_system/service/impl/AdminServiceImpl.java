package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.UserVO;
import com.example.volunteer_system.service.AdminService;
import com.example.volunteer_system.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper,Users> implements AdminService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private void checkRole(){//非普通用户
        int role = UserContext.getRole();
        if(role!=0&&role!=1){
            throw new TokenException("该账号权限不足");
        }
    }
    private void checkUsername(String username){
        if(username==null|| username.isEmpty()){
            throw new TokenException("用户名不存在");
        }
        Users users = this.lambdaQuery()
                .eq(Users::getUsername,username)
                .select(Users::getRole)
                .one();
        if(users==null){
            throw new TokenException("用户不存在");
        }
        if(users.getRole()!=2){
            throw new TokenException("没有权限操作该账号");
        }
    }
    @Override
    @Transactional
    public void banUser(String username){
        checkRole();
        checkUsername(username);
        this.lambdaUpdate()
                .eq(Users::getUsername,username)
                .set(Users::getStatus,2)
                .update();
        Users target =this.lambdaQuery().eq(Users::getUsername,username).one();
        if(target!=null){
            stringRedisTemplate.opsForValue()
                    .set("user:ban:" + target.getId(), "1", Duration.ofDays(7));
            stringRedisTemplate.delete("session:"+target.getId());
        }
    }
    @Override
    @Transactional
    public void unbanUser(String username){
        checkRole();
        checkUsername(username);
        this.lambdaUpdate()
                .eq(Users::getUsername,username)
                .set(Users::getStatus,1)
                .update();
        Users target =this.lambdaQuery().eq(Users::getUsername,username).one();
        if(target!=null){
            stringRedisTemplate.delete("user:ban:" + target.getId());
        }
    }
    @Override
    public List<UserVO> getUser(){
        checkRole();
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
    public void changeRole(String username,int value){
        checkUsername(username);
        Users target =this.lambdaQuery().eq(Users::getUsername,username).one();
        int role = UserContext.getRole();
        if(role!=0){
            throw new TokenException("该账号权限不足");
        }
        if(value!=1&&value!=2){
            throw new TokenException("修改权限异常，请重试");
        }
        this.lambdaUpdate()
                .eq(Users::getUsername,username)
                .set(Users::getRole,value)
                .update();
        stringRedisTemplate.delete("session:"+ target.getId());
    }

}
