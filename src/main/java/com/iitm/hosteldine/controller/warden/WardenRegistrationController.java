package com.iitm.hosteldine.controller.warden;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "${url.warden.in.out.registration}")
@RequiredArgsConstructor
@Slf4j
public class WardenRegistrationController {

    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    private final HostelMasterService hostelMasterService;    
    private final WardenInfoService wardenInfoService;
    
    @Value("${url.warden.in.out.registration}")
    private String baseUrl;
    
    @GetMapping
    public String getMessCardRequestList(@RequestParam Map<String, String> allParams,PaginationForm form,
    		ModelMap map, HttpServletRequest request, ModelMap model) throws Exception {
    	commonResponseUtil.getAdditionalParams(allParams, form);
		Page<WardenInfoDto> wardenInfoList = wardenInfoService.getWardenInOutDetails(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,wardenInfoList , form);
		WardenInfoDto wardenDetails = wardenInfoService.getWardenDetailsByUserName(SecurityCtxUtil.userName());
		model.addAttribute("wardenInfoDto", wardenDetails);
		model.addAttribute("wardenInfo", new WardenInfoDto());
		setAddNewButton(map, baseUrl);
        return HTMLPage.WARDEN_IN_OUT_DETAILS;
    }
    
    @GetMapping("/{id}")
	public String getWardenDetailsById(@PathVariable String id, ModelMap map, HttpServletRequest request)
			throws Exception {
		String formKey = "wardenInfoDto";
		WardenInfoDto wardenInfoDto = new WardenInfoDto();
		if(id.equals("0")) {
			wardenInfoDto = commonResponseUtil.handleModalFormError(request, map,
				formKey, WardenInfoDto.class);
		}else {
			wardenInfoDto = wardenInfoService.getWardenDetailsByInchargeId(id);
			wardenInfoDto.setInchargeDetailsId(Long.parseLong(id));
		}
		map.addAttribute(formKey, wardenInfoDto);
		map.addAttribute("wardenInfo", wardenInfoService.getWardenDetailsByUserName(SecurityCtxUtil.userName()));		
		map.addAttribute("hostelList", hostelMasterService.getHostelList());	
		return HTMLPage.WARDEN_IN_OUT_DETAILS_MODAL;
	}
    
    @PostMapping("${url.save}")
    public String saveOrUpdateWardenRegistration(@ModelAttribute WardenInfoDto wardenInfoDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,ModelMap map, HttpServletRequest request) throws Exception {
    	wardenInfoDto.setInchargeDetailsId(wardenInfoDto.getInchargeDetailsId()!=null ? wardenInfoDto.getInchargeDetailsId() : 0);
    	wardenInfoService.validateWardenRegistration(wardenInfoDto, bindingResult);
        if (bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, wardenInfoDto);
            String id = wardenInfoDto.getInchargeId()!=null ? wardenInfoDto.getInchargeId().toString() : "0";
            return Constants.REDIRECT + baseUrl;
        }
        String status = wardenInfoService.saveOrUpdateWardenRegistration(wardenInfoDto);
        String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ? "message.warden.details.save" : "message.hdc.complaint.failure";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return Constants.REDIRECT + baseUrl;
    }
    
    private void setAddNewButton(ModelMap map , String baseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButton2Properties(true, ModelConstants.BUTTON_PRIMARY,
					messageSource.getMessage("message.button.add.new", null, Locale.getDefault()),
					ModelConstants.FA_ADD_NEW);
			map.addAttribute("addNewUrl", baseUrl
					+ messageSource.getMessage("url.new", null, Locale.getDefault()));
		}
	}
    
    @GetMapping(value = "${url.warden.incharge.details}/{id}")
	public @ResponseBody WardenInfoDto getInchargeDetailsByHostelId(@PathVariable String id) throws Exception{
		return wardenInfoService.getInchargeDetailsByHostelId(id);
	}
}
