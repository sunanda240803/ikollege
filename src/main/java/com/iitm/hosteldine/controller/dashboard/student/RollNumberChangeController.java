package com.iitm.hosteldine.controller.dashboard.student;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.StudentRollnoChangeDto;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.RollNumberValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.roll.no.change.details}")
public class RollNumberChangeController {
	
	private final StudentRollnoChangeService studentRollnoChangeService;
	private final RollNumberValidator rollNumberValidator;
	private final CommonResponseUtil commonResponseUtil;
	
	@GetMapping
	public String getRollNumberChangeDetails(ModelMap map, HttpServletRequest request) throws Exception {
		StudentRollnoChangeDto rollnoDto = studentRollnoChangeService.getRollnoStatus(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("rollnoDto", rollnoDto);
		return HTMLPage.ROLL_NUMBER_DETAILS;
	}
	
	@GetMapping("${url.user.roll.no.details}")
	public String getStudentDetailsForRollnoChange(ModelMap map, HttpServletRequest request) throws Exception {
		map.addAttribute("studentRollnoChange", new StudentRollnoChangeDto());
		commonResponseUtil.updateCommonModelAttributes(map, request, null, null);
		return HTMLPage.ROLL_NUMBER_DETAILS_MODAL;
	}

	@PostMapping("${url.change.roll.no}")
	public ResponseEntity<Object> saveAndUpdate(@ModelAttribute StudentRollnoChangeDto studentRollnoChangeDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs, BindingResult result) throws Exception {

		rollNumberValidator.validate(studentRollnoChangeDto, result);
		if (result.hasErrors()) {
			return commonResponseUtil.handleValidationErrors(result);
		}
		
		String saveStatus = studentRollnoChangeService.saveAndUpdate(studentRollnoChangeDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		BaseResponse baseResponse = (BaseResponse) redirectAttrs.getFlashAttributes().get(Constants.RESPONSE);
		return ResponseEntity.ok(baseResponse);
	}
	
	@GetMapping("${url.check.roll.no.exist}")
    public @ResponseBody String checkRollNoExist(@RequestParam String rollno) throws Exception {
        return studentRollnoChangeService.checkRollNoExist(rollno);
    }
}
