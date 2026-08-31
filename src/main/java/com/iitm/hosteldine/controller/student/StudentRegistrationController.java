package com.iitm.hosteldine.controller.student;

import java.util.Base64;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentRegistrationController {

	private final CommonResponseUtil commonResponseUtil;
	private final StudentBioDataService studentBioDataService;
	private final SimsConfigDataService simsConfigDataService;

	@GetMapping(value = { "${url.student.registration}" + "${url.get}", "${url.admin.student.bio.data}" + "${url.update}" })
	public String getStudentRegistrationById(@RequestParam(required = false) String studentId, ModelMap map,
			HttpServletRequest request) throws Exception {
		StudentBioDataFormDetailDto bioDataDto = studentBioDataService
				.getStudentRegistrationById(studentId != null ? studentId : SecurityCtxUtil.userId().toUpperCase());

		map.addAttribute("form", bioDataDto);
		map.addAttribute("proofTypeList",
				simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.PROOF_TYPES));
		if (bioDataDto.getImageBytes() != null) {
			String encodedImage = Base64.getEncoder().encodeToString(bioDataDto.getImageBytes());
			map.addAttribute("studentProfile", encodedImage);
		}
		if (bioDataDto.getStudentSignBytes() != null) {
			String encodedImage = Base64.getEncoder().encodeToString(bioDataDto.getStudentSignBytes());
			map.addAttribute("studentSignature", encodedImage);
		}
		if (bioDataDto.getParentSignBytes() != null) {
			String encodedImage = Base64.getEncoder().encodeToString(bioDataDto.getParentSignBytes());
			map.addAttribute("parentSignature", encodedImage);
		}
		String action = studentBioDataService.isStudentDataFilled(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("action", action == null ? "view" : "update");
		String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
		map.addAttribute("guideEmail", guideEmail);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		map.addAttribute("loginType", "update");
		map.addAttribute("edit", false);
		return HTMLPage.STUDENT_REGISTRATION;
	}

	@GetMapping("${url.get.applicationNo}")
	public String checkApplicationNoExist(ModelMap map, HttpServletRequest request) {
		map.addAttribute("form", new StudentBioDataFormDetailDto());
		return HTMLPage.STUDENT_APPLICATION_NUMBER;
	}

	@GetMapping("${url.student.registration}" + "${url.applicationNo.exists}")
	public @ResponseBody boolean checkApplicationNoExist(@RequestParam String applicationNo) {
		return studentBioDataService.checkApplicationNumberExist(applicationNo);
	}
}
