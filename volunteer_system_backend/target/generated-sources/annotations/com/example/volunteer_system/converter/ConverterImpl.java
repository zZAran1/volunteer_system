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
    date = "2026-09-02T01:44:44+0800",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ConverterImpl implements Converter {

    @Override
    public Users toUser(RegisterDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Users users = new Users();

        users.setEmail( dto.getEmail() );
        users.setPassword( dto.getPassword() );
        users.setUsername( dto.getUsername() );

        return users;
    }

    @Override
    public ProfileVO toProfileVO(Users user) {
        if ( user == null ) {
            return null;
        }

        ProfileVO profileVO = new ProfileVO();

        profileVO.setCreated_at( user.getCreated_at() );
        profileVO.setEmail( user.getEmail() );
        profileVO.setImage_url( user.getImage_url() );
        profileVO.setRole( user.getRole() );
        profileVO.setUsername( user.getUsername() );

        return profileVO;
    }

    @Override
    public UserVO toUsersVO(Users users) {
        if ( users == null ) {
            return null;
        }

        UserVO userVO = new UserVO();

        userVO.setCreated_at( users.getCreated_at() );
        userVO.setEmail( users.getEmail() );
        userVO.setId( users.getId() );
        userVO.setRole( users.getRole() );
        userVO.setStatus( users.getStatus() );
        userVO.setUsername( users.getUsername() );

        return userVO;
    }

    @Override
    public Activity toActivity(CreateActivityDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Activity activity = new Activity();

        activity.setAddress( dto.getAddress() );
        activity.setDescription( dto.getDescription() );
        activity.setEnd_date( dto.getEnd_date() );
        activity.setHeadcount_limit( dto.getHeadcount_limit() );
        activity.setStart_date( dto.getStart_date() );
        activity.setTitle( dto.getTitle() );

        return activity;
    }

    @Override
    public Activity toActivity(UpdateActivityDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Activity activity = new Activity();

        activity.setAddress( dto.getAddress() );
        activity.setDescription( dto.getDescription() );
        activity.setEnd_date( dto.getEnd_date() );
        activity.setHeadcount_limit( dto.getHeadcount_limit() );
        activity.setId( dto.getId() );
        activity.setStart_date( dto.getStart_date() );
        activity.setTitle( dto.getTitle() );

        return activity;
    }

    @Override
    public PersonalVO toPersonalVO(Personal personal) {
        if ( personal == null ) {
            return null;
        }

        PersonalVO personalVO = new PersonalVO();

        personalVO.setAge( personal.getAge() );
        personalVO.setCurrent_address( personal.getCurrent_address() );
        personalVO.setEmail( personal.getEmail() );
        personalVO.setGender( personal.getGender() );
        personalVO.setIdNumber( personal.getIdNumber() );
        personalVO.setPhone( personal.getPhone() );
        personalVO.setRealName( personal.getRealName() );

        return personalVO;
    }
}
