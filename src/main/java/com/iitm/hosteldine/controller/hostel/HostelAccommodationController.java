package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dashboard.student.StudentFilesInfoDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
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
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelAccommodationService;
import com.iitm.hosteldine.service.student.CompleteStudentApplicationViewService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.HostelAccommodationValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.accommodation}")
public class HostelAccommodationController {

	private final HostelAccommodationService hostelAccommodationService;
	private final HostelAccommodationValidator hostelAccommodationValidator;
	private final CompleteStudentApplicationViewService completeStudentApplicationViewService;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;
	private final StudentAppointmentRequestService studentAppointmentRequestService;
	private final MessageSource messageSource;

	@Value("${url.hostel.accommodation}")
	private String baseUrl;
	@Value("${url.hostel.accommodation}" + "${url.list}")
	private String getStudentHostelAccommodationList;
	@Value("${url.hostel.accommodation}" + "${url.new}")
	private String getStudentHostelAccommodationForm;

	@GetMapping("${url.list}")
	public String getStudentHostelAccommodationList(PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		Page<StudentAppointmentRequestDto> hostelAccommodationList = hostelAccommodationService
				.getStudentHostelAccommodationList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, hostelAccommodationList, form);
		return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_LIST;
	}

	@GetMapping("${url.new}")
	public String addNewStudentHostelAccommodationRequest(ModelMap map, HttpServletRequest request) throws Exception {
		map.addAttribute("studentAppointmentRequestDto", new StudentAppointmentRequestDto());
		map.addAttribute("natureOfAppointment", simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT));
		String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
		map.addAttribute("guideEmail",guideEmail);
		return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_FORM;
	}

	@PostMapping("${url.save}")
	public String saveStudentHostelAccommodationRequest(@ModelAttribute StudentAppointmentRequestDto studentAppointmentRequestDto,
														BindingResult bindingResult,RedirectAttributes redirectAttrs, HttpServletRequest request,
                                                        ModelMap map) throws Exception {
		hostelAccommodationValidator.validate(studentAppointmentRequestDto, bindingResult);
		if (bindingResult.hasErrors()) {
            map.addAttribute("natureOfAppointment", simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT));
			return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_FORM;
		}
		try {
			String saveStatus = hostelAccommodationService.saveStudentHostelAccommodationRequest(studentAppointmentRequestDto, request);
			commonResponseUtil.updateSaveAndErrorResponseByStatus(saveStatus, redirectAttrs,
					saveStatus != null ? "response.save.success" : "message.label.ccw.configure.not.found");
		} catch (Exception e){
			commonResponseUtil.exceptionMessageHandling(e,redirectAttrs);
		}
		return Constants.REDIRECT + getStudentHostelAccommodationList;
	}

	@GetMapping("${url.exist}")
	public @ResponseBody String checkApprovalDate(@RequestParam LocalDate appointmentFrom,
			@RequestParam LocalDate appointmentTo, @RequestParam(required = false) Long requestId) throws Exception {
		return completeStudentApplicationViewService.checkApprovalDate(appointmentFrom, appointmentTo, requestId);
	}

	@GetMapping("${url.resend.mail}" + "${id}")
	public String getResendMail(ModelMap map, @PathVariable String id, HttpServletRequest request)
			throws Exception {
		map.addAttribute("requestId", id);
		return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_RESEND_MAIL;
	}

	@PostMapping("${url.resend.mail}")
	public String saveResendMail(@ModelAttribute StudentAppointmentRequestDto studentAppointmentRequestDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = hostelAccommodationService.resendMail(studentAppointmentRequestDto.getRequestId(), request);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, "response.mail.success");
		return Constants.REDIRECT + getStudentHostelAccommodationList;
	}

	@GetMapping("${url.cancel}" + "${id}" + "${status}")
	public String getCancelRequest(ModelMap map, @PathVariable String id, @PathVariable String status,
			HttpServletRequest request) throws Exception {
		map.addAttribute("requestId", id);
		map.addAttribute("status", status);
		return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_CANCELLATION;
	}

	@PostMapping("${url.cancel}")
	public String saveCancelRequest(@ModelAttribute StudentAppointmentRequestDto studentAppointmentRequestDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = hostelAccommodationService.saveCancelRequest(studentAppointmentRequestDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, "response.cancel.success");
		return Constants.REDIRECT + getStudentHostelAccommodationList;
	}

	@GetMapping("${url.pdf.download}" + "${RequestId}")
	public ResponseEntity<Resource> downloadStudentBioDataPDF(@PathVariable String RequestId) throws Exception {
	    Long decryptedRequestId = MCrypt.getInstance().decryptToLong(RequestId);
        String studentId = SecurityCtxUtil.userId().toUpperCase();
	    Resource resource = hostelAccommodationService.generatePdf(studentId, decryptedRequestId);
		return Utility.prepareDownloadFile(resource);
	}

	@GetMapping("${url.get.view}" + ModelConstants.URL_ID)
	public String viewStudentHostelAccommodationRequest(@PathVariable(ModelConstants.ID) String id, ModelMap model) throws Exception {
		long encryptedRequestId = Long.parseLong(MCrypt.getInstance().decryptToString(id));
		String approvedStatus = studentAppointmentRequestService.getRequestIdStatus(encryptedRequestId).getStatus();
		boolean status = approvedStatus.equals(WorkflowStatus.VALIDATING.getStatus());
		StudentAppointmentRequestDto studentAppointmentRequestDto = studentAppointmentRequestService.getStudentAppointmentRequestById(encryptedRequestId);
		List<StudentWorkflowDto> studentWorkflowDtoList = studentAppointmentRequestService.getByStudentIdAndRequestIdAndActiveFlag(encryptedRequestId, studentAppointmentRequestDto.getStudentId(), WorkflowStatus.DEFAULT.getStatus());
		List<StudentFilesInfoDto> studentFileInfoDtoList = studentAppointmentRequestService.getStudentFileInfoList(encryptedRequestId, studentAppointmentRequestDto.getStudentId());
		studentAppointmentRequestDto.setStudentWorkflowDto(studentWorkflowDtoList);
		model.addAttribute(messageSource.getMessage(StudentConstants.STD_APPOINTMENT_DTO.getStudentConstant(),
				null, Locale.getDefault()), studentAppointmentRequestDto);
		model.addAttribute("studentFileInfoDtoList", studentFileInfoDtoList);
		model.addAttribute("status", status);
		return HTMLPage.STUDENT_HOSTEL_ACCOMMODATION_VIEW;
	}
}
