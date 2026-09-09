package com.example.volunteer_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.volunteer_system.model.dto.RegistrationDTO;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    List<ActivityVO> adminSelectAllActivity();
    List<ActivityVO> SelectActivity(@Param("statusId") Integer statusId);
    List<ActivityVO> selectMyActivity(@Param("posterId") Integer posterId);
    List<ActivityVO> selectRegistered(@Param("MyId") Integer Id);
    void refreshEnded();
    void refreshOngoing();
    void refreshFull();
    void refreshUnFull();
}
