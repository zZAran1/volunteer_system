package com.example.volunteer_system.converter;

import com.example.volunteer_system.model.dto.CreateActivityDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateActivityDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.entity.Personal;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.model.vo.PersonalVO;
import com.example.volunteer_system.model.vo.ProfileVO;
import com.example.volunteer_system.model.vo.UserVO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-04T01:08:49+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ConverterImpl implements Converter {

    @Override
    public Users toUser(RegisterDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Users users = new Users();

        users.setUsername( dto.getUsername() );
        users.setPassword( dto.getPassword() );
        users.setEmail( dto.getEmail() );

        return users;
    }

    @Override
    public ProfileVO toProfileVO(Users user) {
        if ( user == null ) {
            return null;
        }

        ProfileVO profileVO = new ProfileVO();

        profileVO.setUsername( user.getUsername() );
        profileVO.setEmail( user.getEmail() );
        profileVO.setCreated_at( user.getCreated_at() );
        profileVO.setRole( user.getRole() );
        profileVO.setImage_url( user.getImage_url() );

        return profileVO;
    }

    @Override
    public UserVO toUsersVO(Users users) {
        if ( users == null ) {
            return null;
        }

        UserVO userVO = new UserVO();

        userVO.setId( users.getId() );
        userVO.setUsername( users.getUsername() );
        userVO.setEmail( users.getEmail() );
        userVO.setCreated_at( users.getCreated_at() );
        userVO.setRole( users.getRole() );
        userVO.setStatus( users.getStatus() );

        return userVO;
    }

    @Override
    public Activity toActivity(CreateActivityDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Activity activity = new Activity();

        activity.setTitle( dto.getTitle() );
        activity.setAddress( dto.getAddress() );
        activity.setStart_date( dto.getStart_date() );
        activity.setEnd_date( dto.getEnd_date() );
        activity.setDescription( dto.getDescription() );
        activity.setHeadcount_limit( dto.getHeadcount_limit() );

        return activity;
    }

    @Override
    public Activity toActivity(UpdateActivityDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Activity activity = new Activity();

        activity.setId( dto.getId() );
        activity.setTitle( dto.getTitle() );
        activity.setAddress( dto.getAddress() );
        activity.setStart_date( dto.getStart_date() );
        activity.setEnd_date( dto.getEnd_date() );
        activity.setDescription( dto.getDescription() );
        activity.setHeadcount_limit( dto.getHeadcount_limit() );

        return activity;
    }

    @Override
    public PersonalVO toPersonalVO(Personal personal) {
        if ( personal == null ) {
            return null;
        }

        PersonalVO personalVO = new PersonalVO();

        personalVO.setRealName( personal.getRealName() );
        personalVO.setEmail( personal.getEmail() );
        personalVO.setPhone( personal.getPhone() );
        personalVO.setAge( personal.getAge() );
        personalVO.setGender( personal.getGender() );
        personalVO.setIdNumber( personal.getIdNumber() );
        personalVO.setCurrent_address( personal.getCurrent_address() );

        return personalVO;
    }
}
