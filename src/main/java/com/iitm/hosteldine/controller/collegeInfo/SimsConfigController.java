package com.iitm.hosteldine.controller.collegeInfo;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.collegeInfo.SimsConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.sims.config}")
public class SimsConfigController {
	
	private final SimsConfigService simsConfigService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.sims.config}")
	private String getSimsConfig;

	@GetMapping
	public String getSimsConfigList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Page<SimsConfigDataDto> simsList = simsConfigService.getSimsConfigList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,simsList , form);
		return HTMLPage.SIMS_CONFIG;
	}
	
	@GetMapping("${id}")
	public String getSimsConfigById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		 String formKey = "simsDto";
		 SimsConfigDataDto simsDto = commonResponseUtil.handleModalFormError(request,map,formKey, SimsConfigDataDto.class);

	        if (Objects.nonNull(id) && id > 0) {
	        	simsDto = simsConfigService.getSimsConfigById(id);
	        }

	        map.addAttribute(formKey, simsDto);
		return HTMLPage.SIMS_CONFIG_MODAL;
	}
	
	
	@PostMapping
	public String saveUpdateSimsConfig(@Valid @ModelAttribute  SimsConfigDataDto dto,BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		simsConfigService.validateConfigKey(dto, bindingResult);
		 if(bindingResult.hasErrors()){
	            commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs,bindingResult,dto);
	            return Constants.REDIRECT + getSimsConfig;
	        }
		String saveStatus = simsConfigService.saveUpdateSimsConfig(dto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + getSimsConfig;
	}
	

	
	
	@DeleteMapping("${id}")
    public @ResponseBody BaseResponse deleteSims(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(simsConfigService.deleteSims(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus(Constants.FAILURE);
            return errorResponse;
        }
    }
	
	
	@GetMapping("${url.config.key.exist}" + "${configKey}"+ "${id}")
	public ResponseEntity<?> checkConfigKeyExists(@PathVariable String configKey,@PathVariable long id) {
		boolean configKeys = simsConfigService.checkConfigKeyExists(configKey,id);
		return ResponseEntity.ok().body(configKeys);
	}
	
}
