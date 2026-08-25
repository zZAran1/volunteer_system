package com.example.volunteer_system.service;

import com.example.volunteer_system.model.vo.CaptchaVO;

public interface CaptchaService {
    CaptchaVO generateCaptcha();
    void verifyCaptcha(String captchaId,String captchaText);
}
