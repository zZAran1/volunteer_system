package com.example.volunteer_system.controller;
import com.example.volunteer_system.model.dto.AvatarUpdateDTO;
import com.example.volunteer_system.model.dto.LoginDTO;
import com.example.volunteer_system.model.dto.RegisterDTO;
import com.example.volunteer_system.model.dto.UpdateProfileDTO;
import com.example.volunteer_system.model.vo.CaptchaVO;
import com.example.volunteer_system.model.vo.LoginVO;
import com.example.volunteer_system.model.vo.ProfileVO;
import com.example.volunteer_system.result.Result;
import com.example.volunteer_system.service.CaptchaService;
import com.example.volunteer_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final CaptchaService captchaService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        CaptchaVO vo = captchaService.generateCaptcha();
        return Result.success(vo);
    }
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        captchaService.verifyCaptcha(dto.getCaptchaId(), dto.getCaptchaText());
        LoginVO loginVO=new LoginVO();
        loginVO.setToken(userService.login(dto));
        return Result.success("登录成功",loginVO);
    }
    @PutMapping("/updateProfile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        userService.updateProfile(dto);
        return Result.success("修改成功");
    }
    @PutMapping("/avatarUpdate")
    public Result<Void> avatarUpdate(@RequestParam("file") MultipartFile file) {
        userService.avatarUpdate(file);
        return Result.success("修改成功");
    }
    @GetMapping("/profile")
    public Result<ProfileVO> getProfile() {
        return Result.success(userService.getProfile());
    }
    @DeleteMapping("/deleteUser")
    public Result<Void> deleteUser() {
        userService.deleteUser();
        return Result.success("注销成功");
    }
}
