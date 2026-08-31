package com.iitm.hosteldine.controller.otherCandidate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.OtherCandidate.OtherCandidateService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.other.candidate.user}")
public class OtherUserController {
	
	private final OtherCandidateService otherCandidateService;
	private final CommonResponseUtil commonResponseUtil;
	private final CommonService commonService;
	
	
	@Value("${url.other.candidate.user}")
	private String otherCandidate;
	
	@Value("${url.index}")
	private String index;
	
	@GetMapping
	public String otherCandidateProfile(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		String formKey = "profileDto";
		CandidateProfileDto profileDto = new CandidateProfileDto();
		map.addAttribute(formKey, profileDto);
		return HTMLPage.OTHER_CANDIDATE_PROFILE;
	}
	
	@GetMapping("/{id}")
	public String getOtherCandidateDetails(@PathVariable String id, ModelMap map, HttpServletRequest request, Model model)
			throws Exception {
		long userId = Long.parseLong(MCrypt.getInstance().decryptToString(id));
		String formKey = "profileDto";
		CandidateProfileDto profileDto = new CandidateProfileDto();
		commonResponseUtil.updateCommonModelAttributes(map, request, null, null);
		boolean hasFormErrors = request.getSession().getAttribute(Constants.FORM_ERROR) != null;
		if(userId==0 || hasFormErrors) {
			profileDto = commonResponseUtil.handleModalFormError(request, map,
				formKey, CandidateProfileDto.class);
		}else {
			profileDto = otherCandidateService.getOtherCandidateDetails(userId);
		}
		profileDto.setUserId(userId);
		otherCandidateService.populateCandidatePostData(profileDto);
		//Updating minimum date for the Date of Birth with minimum 15 years from current date
		profileDto.setMaxDate(commonService.getMaxDate(15));
		model.addAttribute(formKey, profileDto);
		request.getSession().setAttribute("encryptedUserId", MCrypt.getInstance().encryptToText(SecurityCtxUtil.userId()));
		return HTMLPage.OTHER_CANDIDATE_PROFILE;
	}

	
	@PostMapping
	public String saveUpdateOtherCandidate(ModelMap map, @ModelAttribute CandidateProfileDto profileDto,
			BindingResult bindingResult,HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		
		otherCandidateService.validateOtherCandidate(profileDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, profileDto);
			return Constants.REDIRECT + otherCandidate +"/"+MCrypt.getInstance().encryptToText(String.valueOf(profileDto.getUserId()));
		}
		String saveStatus = otherCandidateService.saveUpdateOtherCandidate(profileDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		String encryptedRequestId = MCrypt.getInstance().encryptToText(profileDto.getUserId().toString());
		if(Objects.nonNull(profileDto.getUserId()) && profileDto.getUserId()>0) {
			return Constants.REDIRECT + index;		
		}else {
			return Constants.REDIRECT + otherCandidate +"/"+encryptedRequestId;						
		}
	}
}
