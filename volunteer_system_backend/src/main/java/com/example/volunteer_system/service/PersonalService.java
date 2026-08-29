package com.example.volunteer_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.volunteer_system.model.dto.PersonalDTO;
import com.example.volunteer_system.model.entity.Personal;
import com.example.volunteer_system.model.vo.PersonalVO;

public interface PersonalService extends IService<Personal> {
    void createPersonal();
    void updatePersonal(PersonalDTO dto);
    PersonalVO getPersonal();
}
