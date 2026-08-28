package com.example.volunteer_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.volunteer_system.model.entity.Activity;
import com.example.volunteer_system.model.vo.ActivityVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityMapper extends BaseMapper<Activity> {
    List<ActivityVO> selectAllActivity();
    List<ActivityVO> selectMyActivity(@Param("posterId") Integer posterId);
}
