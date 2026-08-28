package com.example.volunteer_system.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.UUID;
import com.example.volunteer_system.exception.CaptchaException;
import com.example.volunteer_system.model.vo.CaptchaVO;
import com.example.volunteer_system.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final int CAPTCHA_TTL_SECONDS = 300; // 5分钟
    private final StringRedisTemplate redisTemplate;
    @Override
    public CaptchaVO generateCaptcha(){
        // 1. 生成图片（Hutool）
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(130, 48, 4, 10);
        String code = captcha.getCode();
        String imageBase64 = captcha.getImageBase64();

        // 2. 存入 Redis（Key = captcha:uuid, Value = 验证码文本）
        String captchaId = UUID.randomUUID().toString();
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        redisTemplate.opsForValue().set(redisKey, code, CAPTCHA_TTL_SECONDS, TimeUnit.SECONDS);

        // 3. 组装返回 VO
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaId(captchaId);
        vo.setImageBase64("data:image/png;base64," + imageBase64);
        return vo;
    }
    public void verifyCaptcha(String captchaId,String captchaText){
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        String savedCode = redisTemplate.opsForValue().get(redisKey);

        if (savedCode == null) {
            throw new CaptchaException("验证码已过期，请刷新重试");
        }
        if (!savedCode.equalsIgnoreCase(captchaText)) {
            throw new CaptchaException("验证码错误");
        }
        // 校验通过立即删除
        redisTemplate.delete(redisKey);

    }
}
