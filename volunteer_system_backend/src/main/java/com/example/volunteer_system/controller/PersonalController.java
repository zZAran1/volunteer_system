package com.example.volunteer_system.controller;

import com.example.volunteer_system.model.dto.PersonalDTO;
import com.example.volunteer_system.model.vo.PersonalVO;
import com.example.volunteer_system.result.Result;
import com.example.volunteer_system.service.PersonalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
@Validated
public class PersonalController {
    private final PersonalService personalService;
    @PostMapping("/createPersonal")
    public Result<Void> createPersonal() {
        personalService.createPersonal();
        return Result.success("/创建个人信息成功");
    }
    @PutMapping("/updatePersonal")
    public Result<Void> updatePersonal(@Validated @RequestBody PersonalDTO dto) {
        personalService.updatePersonal(dto);
        return Result.success("更新信息成功");
    }
    @GetMapping("/getPersonal")
    public Result<PersonalVO> getPersonal(){
        return Result.success(personalService.getPersonal());
    }
}
