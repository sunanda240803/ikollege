package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.constant.CaptchaUtil;
import com.iitm.hosteldine.dto.CaptchaDto;
import com.iitm.hosteldine.dto.CaptchaResponseDto;
import com.iitm.hosteldine.service.CaptchaService;
import com.iitm.hosteldine.util.MCrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


@Controller
@RequiredArgsConstructor
@RequestMapping(value="${url.captcha}")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public @ResponseBody CaptchaResponseDto getCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String captchaCode = CaptchaUtil.getCode(6);
            String captchaToken = MCrypt.getInstance()
                    .encryptToText(request.getSession().getId() + "_" + captchaCode + "_" + System.currentTimeMillis());

            BufferedImage bi = CaptchaUtil.genCaptcha(captchaCode);
            boolean status = captchaService.saveCaptchaDetails(captchaToken, captchaCode);
            if (status) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpg", baos);
                byte[] bytes = baos.toByteArray();
                CaptchaResponseDto cb = new CaptchaResponseDto();
                cb.setImage(bytes);
                cb.setToken(captchaToken);
                return cb;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @PostMapping
    public @ResponseBody CaptchaResponseDto validateCaptcha(@ModelAttribute CaptchaDto captchaDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return captchaService.validateCaptcha(captchaDto.getToken(), captchaDto.getCaptchaCode());
    }
}
