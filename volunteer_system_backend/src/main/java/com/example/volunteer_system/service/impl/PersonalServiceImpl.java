package com.example.volunteer_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.volunteer_system.converter.Converter;
import com.example.volunteer_system.exception.ProfileException;
import com.example.volunteer_system.mapper.PersonalMapper;
import com.example.volunteer_system.model.dto.PersonalDTO;
import com.example.volunteer_system.model.entity.Personal;
import com.example.volunteer_system.model.vo.PersonalVO;
import com.example.volunteer_system.service.PersonalService;
import com.example.volunteer_system.util.UserContext;
import org.springframework.stereotype.Service;

@Service
public class PersonalServiceImpl extends ServiceImpl<PersonalMapper, Personal> implements PersonalService {
    @Override
    public void createPersonal(PersonalDTO dto){
        int user_id= UserContext.getUserId();
        if(this.lambdaQuery().eq(Personal::getUser_id,user_id)!=null){
            throw new ProfileException("每个账号对应一份个人真实信息");
        }
        Personal personal = Converter.INSTANCE.toPersonal(dto);
        personal.setUser_id(user_id);
        this.save(personal);
    }
    @Override
    public void updatePersonal(PersonalDTO dto){
        int user_id= UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Personal::getUser_id,user_id)
                .set(Personal::getAge,dto.getAge())
                .set(Personal::getGender,dto.getGender())
                .set(Personal::getEmail,dto.getEmail())
                .set(Personal::getPhone,dto.getPhone())
                .set(Personal::getCurrent_address,dto.getCurrent_address())
                .set(Personal::getRealName,dto.getRealName())
                .set(Personal::getIdNumber,dto.getIdNumber())
                .update();
    }
    @Override
    public PersonalVO getPersonal(){
        int user_id= UserContext.getUserId();
        Personal personal=this.lambdaQuery()
                .eq(Personal::getUser_id,user_id)
                .one();
        return Converter.INSTANCE.toPersonalVO(personal);
    }

}
