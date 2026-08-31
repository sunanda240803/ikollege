package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.config.DynamicSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelController {
    private final DynamicSecurityService dynamicSecurityService;

    @ModelAttribute
    public void globalModel(ModelMap model) {
        model.addAttribute("institute", dynamicSecurityService.getSchoolGeographyInfoDto());
    }
}
