package com.example.volunteer_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.volunteer_system.model.entity.Personal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonalMapper extends BaseMapper<Personal> {
}
