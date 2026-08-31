package com.iitm.hosteldine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommonController {

    @Value("${build.version}")
    private String buildVersion;

    public void updateCommonAttributes(ModelMap model) {
        model.addAttribute("buildVersion", buildVersion);
    }

}
