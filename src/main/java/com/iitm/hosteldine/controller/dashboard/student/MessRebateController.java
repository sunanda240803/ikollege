package com.iitm.hosteldine.controller.dashboard.student;

import java.time.LocalDate;
import java.util.Locale;

import com.iitm.hosteldine.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.mess.MessRebateDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.mess.MessRebateService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.UrlUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.MessRebateValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.mess.rebate}")
public class MessRebateController {

	private final MessRebateService messRebateService;
	private final StudentBioDataService studentBioDataService;
	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
    private final MessRebateValidator messRebateValidator;
	private final MessageSource messageSource;

	@Value("${url.student.mess.rebate}" + "${url.list}")
	private String getStudentMessRebateList;
	
	@Value("${url.dean.approval.mess.rebate}")
	private String deanMessRebateBaseUrl;

	@GetMapping("${url.widget}")
	public String getLastestStudentMessRebateStatus(ModelMap map) throws Exception {
		String approvalStatus = messRebateService
				.getLastestStudentMessRebateStatus(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("approvalStatus", approvalStatus);
		return HTMLPage.MESS_REBATE;
	}

	@GetMapping("${url.list}")
	public String getStudentMessRebateList(PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		Page<MessRebateDto> messRebatelist = messRebateService.getStudentMessRebateList(form);
		boolean isGuide = studentBioDataService.isStudentGuideNameEmailFilled(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute(StudentConstants.IS_GUIDE_NAME_EMAIL_EMPTY.getStudentConstant(), isGuide);
		commonResponseUtil.updateCommonModelAttributes(map, request, messRebatelist, form);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null && !isGuide) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_GREEN,
					messageSource.getMessage("message.button.add.new", null, Locale.getDefault()),
					ModelConstants.FA_ADD_NEW);
		}
		return HTMLPage.STUDENT_MESS_REBATE_LIST;
	}

	@GetMapping("${url.new}")
	public String addNewStudentMessRebateRequest(ModelMap map, HttpServletRequest request) throws Exception {
		String formKey = "messRebateDto";
		MessRebateDto messRebateDto = commonResponseUtil.handleModalFormError(request, map, formKey,
				MessRebateDto.class);
		map.addAttribute(formKey, messRebateDto);
		map.addAttribute("minFromDate", simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MINIMUM_FROM_DATE));
		map.addAttribute("maxFromDate", simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MAXIMUM_FROM_DATE));
		map.addAttribute("minPeriod", simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MINIMUM_PERIOD));
		map.addAttribute("maxPeriod", simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_REBATE_MAXIMUM_PERIOD));
		map.addAttribute("studentDetails", studentBioDataService.getStudentDetails(SecurityCtxUtil.userId().toUpperCase())); 
		return HTMLPage.STUDENT_MESS_REBATE_MODAL;
	}

	@GetMapping("${id}")
	public String getStudentMessRebateById(@PathVariable Long id, ModelMap map) throws Exception {
		map.addAttribute("messRebateDto", messRebateService.getStudentMessRebateById(id));
		map.addAttribute("messRebateWorkflow", messRebateService.getStudentMessRebateWorkflowDetails(id));
		return HTMLPage.VIEW_STUDENT_MESS_REBATE_DETAILS_MODAL;
	}

	@PostMapping("${url.save}")
	public String saveAndUpdate(@ModelAttribute MessRebateDto messRebateDto, BindingResult bindingResult, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		messRebateValidator.validate(messRebateDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, messRebateDto);
			return Constants.REDIRECT + getStudentMessRebateList;
		}
		String url = UrlUtility.getBaseURL(request) + deanMessRebateBaseUrl;
		String saveStatus = messRebateService.saveAndUpdate(messRebateDto, url, request);
		redirectAttrs.addFlashAttribute("messRebateDto", messRebateDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + getStudentMessRebateList;
	}

	@GetMapping("${url.download}" + "${fileName}")
	public ResponseEntity<Resource> downloadReportDocument(@PathVariable String fileName) throws Exception {
		ByteArrayResource resource = messRebateService.downloadFile(fileName);
		return Utility.prepareDownloadFile(resource, fileName);
	}
	
	@GetMapping("${url.exist}")
    public @ResponseBody String checkFromDateAndToDateExists(@RequestParam LocalDate rebateFrom, @RequestParam LocalDate rebateTo) throws Exception {
        return messRebateService.checkFromDateAndToDateExists(rebateFrom, rebateTo);
    }

}
