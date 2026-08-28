package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.BaseBusinessException;
import com.example.volunteer_system.exception.LoginException;
import com.example.volunteer_system.exception.ProfileException;
import com.example.volunteer_system.exception.RegisterException;

import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.dto.AvatarUpdateDTO;
import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateProfileDTO;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.ProfileVO;
import com.example.volunteer_system.service.UserService;
import com.example.volunteer_system.util.BCryptPasswordUtil;
import com.example.volunteer_system.util.JwtUtil;
import com.example.volunteer_system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService  {
    private final BCryptPasswordUtil bCryptPasswordUtil = new BCryptPasswordUtil();
    private final JwtUtil jwtUtil;
    @Value("${file.upload.path}")
    private String uploadPath;
    @Override
    public void register(RegisterDTO dto){
        if(this.lambdaQuery().eq(Users::getEmail,dto.getEmail()).exists()){
            throw new RegisterException("该邮箱已被注册，请返回登录");
        }
        dto.setPassword(bCryptPasswordUtil.hashPassword(dto.getPassword()));
        Users user = Converter.INSTANCE.toUser(dto);
        user.setRole(2);
        user.setStatus(1);
        user.setCreated_at(LocalDate.now());
        this.save(user);
    }
    @Override
    public String login(LoginDTO dto){
        Users db_user=this.lambdaQuery()
                .eq(Users::getEmail,dto.getEmail())
                .one();
        if(db_user==null){
            throw new LoginException("邮箱或密码错误");
        }
        if(bCryptPasswordUtil.checkPassword(dto.getPassword(),db_user.getPassword())){
            if(db_user.getStatus()==null||db_user.getStatus()!=1){
                throw new LoginException("该账号已被封禁");
            }
            return jwtUtil.generateToken(String.valueOf(db_user.getId()),String.valueOf(db_user.getRole()));
        }else{
            throw new LoginException("邮箱或密码错误");
        }
    }
    @Override
    public void updateProfile(UpdateProfileDTO dto){
        int user_id=UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Users::getId,user_id)
                .set(Users::getUsername,dto.getUsername())
                .update();
    }
    @Override
    public void avatarUpdate(MultipartFile file) {
        int user_id=UserContext.getUserId();
        String original=file.getOriginalFilename();
        String ext=(original!=null && original.contains("."))
                ?original.substring(original.lastIndexOf("."))
                : ".png";
        String filename= UUID.randomUUID().toString().replace("-","")+ext;
        File dir=new File(uploadPath);
        if(!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(dir,filename));
        } catch (IOException e) {
            throw new ProfileException("头像上传失败");
        }
        String url="/uploads/" +filename;
        this.lambdaUpdate()
                .eq(Users::getId, user_id)
                .set(Users::getImage_url, url)
                .update();
    }
    @Override
    public ProfileVO getProfile(){
        int user_id= UserContext.getUserId();
        Users db_user=this.lambdaQuery()
                .eq(Users::getId,user_id)
                .one();
        return Converter.INSTANCE.toProfileVO(db_user);
    }
    @Override
    public void deleteUser(){
        int user_id= UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Users::getId,user_id)
                .remove();
    }
}
