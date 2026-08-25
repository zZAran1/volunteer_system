package com.example.volunteer_system.converter;

import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.ActivityVO;
import com.example.volunteer_system.model.vo.ProfileVO;
import com.example.volunteer_system.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface Converter {
    Converter INSTANCE = Mappers.getMapper(Converter.class);
    Users toUser(RegisterDTO dto);
    ProfileVO toProfileVO(Users user);
    UserVO toUsersVO(Users users);
    Activity toActivity(CreateActivityDTO dto);
    Activity toActivity(UpdateActivityDTO dto);

}
