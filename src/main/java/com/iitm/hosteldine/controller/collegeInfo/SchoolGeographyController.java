package com.iitm.hosteldine.controller.collegeInfo;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.college.CollegeInfoContents;
import com.iitm.hosteldine.dto.collegeInfo.SchoolGeographyInfoDto;
import com.iitm.hosteldine.service.collegeInfo.SchoolGeographyInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping(value = "${url.college.info}")
@RequiredArgsConstructor
public class SchoolGeographyController {
    private final CommonResponseUtil commonResponseUtil;
    private final SchoolGeographyInfoService schoolGeographyInfoService;
    private final DynamicSecurityService dynamicSecurityService;


    @Value("${url.college.info}")
    private String baseUrl;

    @GetMapping
    public String getCollegeInfoDetails(ModelMap model, HttpServletRequest request) {
        SchoolGeographyInfoDto schoolGeographyInfoDto = schoolGeographyInfoService.getSchoolDetails();
        commonResponseUtil.updateCommonModelAttributes(model, request);
        model.addAttribute(CollegeInfoContents.SCHOOL_GEOGRAPHY_INFO_FORM_KEY.getConstants(), schoolGeographyInfoDto);
        return HTMLPage.COLLEGE_INFO;
    }

    @PostMapping(value = "${url.save}")
    public String saveOrCollegeInfo(@Valid @ModelAttribute SchoolGeographyInfoDto schoolGeographyInfoDto, BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,schoolGeographyInfoDto);
            return Constants.REDIRECT + baseUrl;
        }
        String status = schoolGeographyInfoService.saveOrUpdateSchoolGeographyInfo(schoolGeographyInfoDto);
        String message = Objects.equals(status, Constants.UPDATED) ? "message.college.info.update" : "message.college.info.error";
        commonResponseUtil.updateSaveResponseByStatus(status.equals(Constants.ERROR) ? null : status, redirectAttributes,message);
        dynamicSecurityService.loadCollegeInfo();
        return Constants.REDIRECT + baseUrl;
    }
}
