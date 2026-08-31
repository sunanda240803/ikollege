package com.iitm.hosteldine.controller.hostel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.HostelEnrollmentConfigurationDto;
import com.iitm.hosteldine.service.hostel.HostelEnrollmentConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.hostel.HostelEnrollmentConfigValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.enrollment.config}")
public class HostelEnrollmentConfigController {

	private final HostelEnrollmentConfigService hostelEnrollmentConfigService;
	private final HostelEnrollmentConfigValidator hostelEnrollmentConfigValidator;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.hostel.enrollment.config}")
	private String baseUrl;

	@GetMapping
	public String getHostelEnrollmentConfigList(ModelMap map, HttpServletRequest request) {
		HostelEnrollmentConfigurationDto hostelEnrollmentDto = hostelEnrollmentConfigService.getEnrollmentDates();
		map.addAttribute("hostelEnrollmentDto", hostelEnrollmentDto);
		commonResponseUtil.updateCommonModelAttributes(map, request, null, null);
		return HTMLPage.HOSTEL_ENROLLMENT_CONFIG;
	}

	@PostMapping
	public String saveOrUpdateHostelConfig(@ModelAttribute HostelEnrollmentConfigurationDto hostelEnrollmentDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs, BindingResult result) throws Exception {
		hostelEnrollmentConfigValidator.validate(hostelEnrollmentDto, result);
		if (result.hasErrors()) {
			return HTMLPage.HOSTEL_ENROLLMENT_CONFIG;
		}
		String saveStatus = hostelEnrollmentConfigService.saveUpdateHostelConfig(hostelEnrollmentDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs,
				"response.hostel.enroll.update.success");
		return Constants.REDIRECT + baseUrl;
	}
}
