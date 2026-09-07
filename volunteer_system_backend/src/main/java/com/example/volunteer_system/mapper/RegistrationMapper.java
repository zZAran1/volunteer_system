package com.example.volunteer_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.volunteer_system.model.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    boolean insertRegistration(@Param("activityId") Integer activityId, @Param("registrantId") Integer registrantId);
}
