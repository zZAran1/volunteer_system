package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.LoginException;
import com.example.volunteer_system.exception.ProfileException;
import com.example.volunteer_system.exception.RegisterException;
import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.mapper.UserMapper;
import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateProfileDTO;
import com.example.volunteer_system.model.entity.Registration;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.ProfileVO;
import com.example.volunteer_system.service.RegistrationService;
import com.example.volunteer_system.service.UserService;
import com.example.volunteer_system.util.BCryptPasswordUtil;
import com.example.volunteer_system.util.JwtUtil;
import com.example.volunteer_system.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService  {
    private final BCryptPasswordUtil bCryptPasswordUtil = new BCryptPasswordUtil();
    @Autowired
    private  JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RegistrationService registrationService;
    @Value("${file.upload.path}")
    private String uploadPath;
    @Override
    public void register(RegisterDTO dto){
        if(this.lambdaQuery().eq(Users::getEmail,dto.getEmail()).exists()){
            throw new RegisterException("邮箱或密码错误");
        }
        if(this.lambdaQuery().eq(Users::getUsername,dto.getUsername()).exists()){
            throw new ProfileException("该用户名已被使用");
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
            stringRedisTemplate.opsForValue()//生成token前在redis中登记会话
                    .set("session:"+db_user.getId(),"1", Duration.ofDays(7));
            return jwtUtil.generateToken(String.valueOf(db_user.getId()),String.valueOf(db_user.getRole()));
        }else{
            throw new LoginException("邮箱或密码错误");
        }
    }
    @Override
    public void updateProfile(UpdateProfileDTO dto){
        int user_id=UserContext.getUserId();
        if(dto.getUsername()==null){
            throw new ProfileException("用户名不能为空");
        }
        Users db_user = this.lambdaQuery()
                .eq(Users::getId,user_id)
                .select(Users::getUsername)
                .one();
        if(db_user==null){
            throw new TokenException("登录失效，请重新登录");
        }
        if(this.lambdaQuery().eq(Users::getUsername,dto.getUsername()).exists()&&!dto.getUsername().equals(db_user.getUsername())){
            throw new ProfileException("该用户名已被使用");
        }
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
        List<String> allowedExt=List.of(".jpg",".jpeg",".webp",".png");
        if(!allowedExt.contains(ext.toLowerCase())){
            throw new ProfileException("头像文件仅支持jpg/jpeg/png/webp 格式");
        }
        String filename= UUID.randomUUID().toString().replace("-","")+ext;
        File dir=new File(uploadPath);
        if(!dir.exists()) dir.mkdirs();
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ProfileException("文件无法解析为图片");
            }
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
    public void logout(){
        int user_id=UserContext.getUserId();
        stringRedisTemplate.delete("session:"+user_id);
    }
    @Override
    @Transactional
    public void deleteUser(){
        int user_id= UserContext.getUserId();
        List<Integer> activityIdList=registrationService.lambdaQuery()
                .eq(Registration::getRegistrant_id,user_id)
                .select(Registration::getActivity_id)
                .list()
                .stream()
                .map(Registration::getActivity_id)
                .distinct()
                .collect(Collectors.toList());
        for(Integer activityId:activityIdList){
            registrationService.unRegistrant(activityId);
        }
        this.lambdaUpdate()
                .eq(Users::getId,user_id)
                .remove();
        stringRedisTemplate.delete("session:"+user_id);
    }
}
