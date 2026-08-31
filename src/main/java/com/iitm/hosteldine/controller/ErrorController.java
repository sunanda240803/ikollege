package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ErrorPageEnum;
import com.iitm.hosteldine.util.HTMLPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequestMapping(value = "${url.error}")
public class ErrorController {
    public static Long ERROR_PAGE_REDIRECT;

    @GetMapping
    public ModelAndView onError(@RequestParam String error, ModelMap model, HttpServletRequest request) {
        log.error(error);
        ErrorPageEnum errorPageEnum = ErrorPageEnum.getByErrorCode(error);
        if (errorPageEnum.getRedirectUrl() != null) {
            if (errorPageEnum == ErrorPageEnum.UNAUTHORIZED) {
                if (!SecurityCtxUtil.userId().equals(SecurityCtxUtil.ANONYMOUS_USER)) {
                    errorPageEnum = ErrorPageEnum.FORBIDDEN;
                }
            }
            model.addAttribute("redirectTo", request.getContextPath() + errorPageEnum.getRedirectUrl());
        }
        model.addAttribute("redirect", errorPageEnum);
        model.addAttribute("redirectDuration", ErrorController.ERROR_PAGE_REDIRECT);
        return new ModelAndView(HTMLPage.ERROR, model);
    }

    @Value("${conf.errorPageRedirect}")
    public void setErrorPageRedirect(Long errorPageRedirect) {
        ErrorController.ERROR_PAGE_REDIRECT = errorPageRedirect;
    }

}
