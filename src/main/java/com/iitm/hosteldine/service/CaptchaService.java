package com.iitm.hosteldine.service;

import com.iitm.hosteldine.dto.CaptchaResponseDto;
import com.iitm.hosteldine.entity.CaptchaEntity;
import com.iitm.hosteldine.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


@Service
public class CaptchaService {

    @Autowired
    CaptchaRepository captchaRepository;

    public boolean saveCaptchaDetails(String captchaToken, String captchaCode) {
        CaptchaEntity captcha = new CaptchaEntity();
        captcha.setToken(captchaToken);
        captcha.setCaptchaCode(captchaCode);
        captcha.setCreatedAt(LocalDateTime.now());
        captchaRepository.save(captcha);
        return true;
    }

    public CaptchaResponseDto validateCaptcha(String token, String captchaCode) {
        CaptchaEntity captcha = captchaRepository.findByToken(token);
        if (captcha != null) {
            if (captcha.getCaptchaCode().equals(captchaCode)) {
                captchaRepository.delete(captcha);
                return new CaptchaResponseDto(200, "Validated successfully", "", null);
            } else {
                return new CaptchaResponseDto(400, "Invalid captcha", "", null);
            }
        } else {
            return new CaptchaResponseDto(400, "Invalid token", "", null);
        }
    }
}
