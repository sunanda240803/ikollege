package com.iitm.hosteldine.controller.warden;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.warden.WardenInfoValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.warden.info}")
@RequiredArgsConstructor
public class WardenInfoController {

	@Value("${url.warden.info}")
	private String baseUrl;

	private final WardenInfoService wardenInfoService;
	private final CommonResponseUtil commonResponseUtil;
	private final HostelMasterService hostelMasterService;
	private final WardenInfoValidator wardenInfoValidator;
	private final SimsConfigDataService simsConfigDataService;

	@RequestMapping(method = { RequestMethod.GET })
	public String getWardenInfoList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		String splitBaseUrl = baseUrl.replace("/", "");
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<WardenInfoDto> wardenInfoList = wardenInfoService.getWardenInfoList(form, splitBaseUrl);
		commonResponseUtil.updateCommonModelAttributes(map, request, wardenInfoList, form);
		return HTMLPage.HOSTEL_WARDEN_INFO;
	}

	@GetMapping("${url.add}")
	public String addWardenInfo(ModelMap map, HttpServletRequest request) throws Exception {
		String formKey = "wardenInfoDto";
		WardenInfoDto wardenInfoDto = commonResponseUtil.handleModalFormError(request, map, formKey, WardenInfoDto.class);
		String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
        map.addAttribute("guideEmail",guideEmail);
		map.addAttribute(formKey, wardenInfoDto);
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.HOSTEL_ADD_WARDEN_INFO;
	}

	@PostMapping(value = "${url.save.warden.info}")
	public String saveWardenInfo(@ModelAttribute WardenInfoDto dto, ModelMap map, BindingResult bindingResult, RedirectAttributes redirectAttrs) throws Exception {
		if (dto.getImageContent() != null)
			dto.setImageBytes(Base64.getDecoder().decode(dto.getImageContent()));
		wardenInfoValidator.validate(dto, bindingResult);
		if (bindingResult.hasErrors()) {
			dto.setImageName(null);
			dto.setImageFileName(null);
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, dto);
			return Constants.REDIRECT + baseUrl;
		}
		boolean status = wardenInfoService.saveWardenInfo(dto);
		commonResponseUtil.updateSaveResponseByStatus(String.valueOf(status), redirectAttrs);
		return Constants.REDIRECT + baseUrl;
	}

	@GetMapping("${url.edit}")
	public String getWardenInfo(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 2, 1);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				Long id = Utility.getLongValueOrDefault(split, 0, null);
				WardenInfoDto wardenInfoDto = wardenInfoService.getWardenInfo(id);
				String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
                map.addAttribute("guideEmail",guideEmail);
				map.addAttribute("hostelList", hostelMasterService.getHostelList());
				map.addAttribute("wardenInfoDto", wardenInfoDto);
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.HOSTEL_ADD_WARDEN_INFO;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deletePaymentAdvice(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 2, 1);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				Long id = Utility.getLongValueOrDefault(split, 0, null);
				boolean deletedStatus = wardenInfoService.deleteWardenInfo(id);
				return CommonResponseUtil.updateResponseByStatus(deletedStatus, "response.delete.success", "response.delete.error");
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

}
