package com.iitm.hosteldine.controller.dashboard.student;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.iitm.hosteldine.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationGuestDetailsDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationRequestDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestFilesInformationDto;
import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataFamilyInfoService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.studentDashboard.GuestAccommodationGuestDetailsService;
import com.iitm.hosteldine.service.studentDashboard.GuestAccommodationRequestService;
import com.iitm.hosteldine.service.studentDashboard.GuestFilesInformationService;
import com.iitm.hosteldine.service.warden.GuestAccommodationChargesService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.guest.accommodation.request}")
public class GuestAccommodationRequestController {
	private final CommonResponseUtil commonResponseUtil;
	private final GuestAccommodationRequestService guestAccommodationRequestService;
	private final StudentDetailsInfoService studentDetailsInfoService;
	private final GuestAccommodationChargesService guestAccommodationChargeService;
	private final StudentBioDataService studentBioDataService;
	private final SimsConfigDataService simsConfigDataService;
	private final GuestFilesInformationService guestFilesInformationService;
	private final GuestAccommodationGuestDetailsService guestAccommodationGuestDetailsService;
	private final StudentBioDataFamilyInfoService studentBioDataFamilyInfoService;
	private final MessageSource messageSource;
	
	@Value("${url.guest.accommodation.request}")
	private String guestAccommodationRequest;

	@Value("${url.guest.student.view}")
	private String guestAccommodationViewStudentRequest;
	
	@GetMapping
	public String getGuestAccommodationRequestList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Page<GuestAccommodationRequestDto> guestAccommodationRequestList = guestAccommodationRequestService.getGuestAccommodationRequestList(form);
		String newId = MCrypt.getInstance().encryptToText(Constants.NEW +"`0`"+ System.currentTimeMillis());
		map.addAttribute("newId", newId);
		commonResponseUtil.updateCommonModelAttributes(map, request ,guestAccommodationRequestList , form);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_GREEN,
					messageSource.getMessage("message.button.add.new", null, Locale.getDefault()),
					ModelConstants.FA_ADD_NEW);
		}
		return HTMLPage.GUEST_ACCOMMODATION_REQUEST;
	}
	
	@GetMapping("${id}")
	public String getGuestAccommodationById(@PathVariable String id, ModelMap map, HttpServletRequest request)
			throws Exception {
		Long requestId = 0L;
		Long parentReqId = 0L;
		String decodedReference = null;
		if (id != null) {
			decodedReference = MCrypt.getInstance().decryptToString(id);
			String[] parts = decodedReference.split(Constants.BACKTICK);
			requestId = parts.length > 1 && parts[1] != null && !parts[1].isEmpty() && !"null".equalsIgnoreCase(parts[1])
					? Long.parseLong(parts[1]) : 0L;
			parentReqId = parts.length > 2 && parts[2] != null && !parts[2].isEmpty() && !"null".equalsIgnoreCase(parts[2]) 
					? Long.parseLong(parts[2]) : 0L;
		}
		StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(SecurityCtxUtil.userId().toUpperCase());
		GuestAccommodationChargesDto guestDto = guestAccommodationChargeService.getGuestAccommodationChargeDetails();

		Map<String, Integer> relationPriority = Map.of(
				Constants.FATHER, 1,
				Constants.MOTHER, 2,
				Constants.BROTHER, 3,
				Constants.SISTER, 4,
				Constants.SPOUSE, 5
		);

		List<StudentBioDataFamilyInfoDto> familyList = studentBioDataService.getStudentFamilyDetailsById().stream()
				.sorted((f1, f2) -> {
					int priority1 = relationPriority.getOrDefault(f1.getRelationType(), Integer.MAX_VALUE);
					int priority2 = relationPriority.getOrDefault(f2.getRelationType(), Integer.MAX_VALUE);

					if ((f1.getRelationType().equals(Constants.BROTHER) && f2.getRelationType().equals(Constants.SISTER))) {
						return Integer.compare(f2.getAge(), f1.getAge());
					}
					if ((f1.getRelationType().equals(Constants.SISTER) && f2.getRelationType().equals(Constants.BROTHER))) {
						return Integer.compare(f1.getAge(), f2.getAge());
					}

					return Integer.compare(priority1, priority2);
				})
				.toList();


		if (decodedReference.startsWith(Constants.NEW)) {
			map.addAttribute("guestRequestDto", new GuestAccommodationRequestDto());
		}else {
			GuestAccommodationRequestDto guestRequestDetails = guestAccommodationRequestService.getGuestAccommodationRequestById(parentReqId);
			List<GuestFilesInformationDto> guestFileInfo = guestFilesInformationService.getFileUploadByRequestId(parentReqId);
			List<GuestAccommodationGuestDetailsDto> guestList = guestAccommodationGuestDetailsService.getGuestList(parentReqId) ;

			getGuestDetails(map, parentReqId, guestRequestDetails, guestFileInfo);
			map.addAttribute("guestList", guestList);
		}
		String maximumDays = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_ACCOMODATION_STAY_MAX_TO_DATE);
		String rules = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_REQUEST_RULES);
		rules=rules.replace("#%individualRoomAmount%#", String.valueOf(guestDto.getIndividualRoomAmount()));
		rules=rules.replace("#%individualRoomMultipleAmount%#", String.valueOf(guestDto.getIndividualRoomMultipleAmount()));
		rules=rules.replace("#%amount%#", String.valueOf(guestDto.getAmount()));
		rules=rules.replace("#%maxNoDays%#", String.valueOf(maximumDays));
		boolean allProofFileNamesNull = familyList.stream().allMatch(f -> f.getProofFileName() == null);
		map.addAttribute("allProofFileNamesNull", allProofFileNamesNull);
		map.addAttribute("rules", rules);
		map.addAttribute("guestDto", guestDto);
		map.addAttribute("studentDetailsInfoDto", studentDetailsInfoDto);
		map.addAttribute("familyList", familyList);
		map.addAttribute("maxNoDays", maximumDays);
		boolean isDisabled = guestAccommodationRequestService.studentHostelAllotted(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("disabled", isDisabled);
		if (decodedReference.startsWith(Constants.NEW)) {
			map.addAttribute("proofTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.PROOF_TYPES));
			return HTMLPage.ADD_GUEST_ACCOMMODATION_REQUEST;
		} else {
			return HTMLPage.VIEW_GUEST_ACCOMMODATION_REQUEST;
		}
		
	}
	
	@PostMapping
	public String saveGuestAccommodationRequest(ModelMap map, @ModelAttribute GuestAccommodationRequestDto guestAccommodationRequestDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = guestAccommodationRequestService.saveGuestAccommodationRequest(guestAccommodationRequestDto, request);
		commonResponseUtil.updateResponse(saveStatus != null ? saveStatus : null, redirectAttrs,
				"message.guest.accommodation.request.saved", "message.label.warden.details.not.present");
		return Constants.REDIRECT + guestAccommodationRequest;
	}
	
	@GetMapping("${url.download}" + "${fileName}")
	public ResponseEntity<Resource> downloadReportDocument(@PathVariable String fileName) throws Exception {
		ByteArrayResource resource = guestFilesInformationService.downloadFile(fileName);
		return Utility.prepareDownloadFile(resource, fileName);
	}
	
	@GetMapping("${url.stay.extension}" + "${id}")
	public String getGuestStayExtensionRequestById(@PathVariable String id, ModelMap map, HttpServletRequest request)
			throws Exception {
		Long requestId = 0L;
		Long parentReqId = 0L;
		String decodedReference = null;
		if (id != null) {
			decodedReference = MCrypt.getInstance().decryptToString(id);
			String[] parts = decodedReference.split(Constants.BACKTICK);
			requestId = parts.length > 1 && parts[1] != null && !parts[1].isEmpty() && !"null".equalsIgnoreCase(parts[1])
					? Long.parseLong(parts[1]) : 0L;
			parentReqId = parts.length > 2 && parts[2] != null && !parts[2].isEmpty() && !"null".equalsIgnoreCase(parts[2]) 
					? Long.parseLong(parts[2]) : 0L;
		}
		StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService
				.getStudentInfoDetails(SecurityCtxUtil.userId().toUpperCase());
		GuestAccommodationRequestDto guestRequestDetails = guestAccommodationRequestService
				.getGuestAccommodationRequestById(parentReqId);
//		List<GuestFilesInformationDto> guestFileInfo = guestFilesInformationService.getFileUploadByRequestId(parentReqId);
		List<GuestAccommodationGuestDetailsDto> guestList = guestAccommodationGuestDetailsService.getGuestList(parentReqId) ;
		String maximumDays = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_ACCOMODATION_STAY_MAX_TO_DATE);
		GuestAccommodationRequestDto stayPeriods = guestAccommodationRequestService.getPreviousStayPeriods(parentReqId);
		getGuestDetails(map, parentReqId, guestRequestDetails, new ArrayList<>());
		map.addAttribute("studentDetailsInfoDto", studentDetailsInfoDto);
		map.addAttribute("guestList", guestList);
		map.addAttribute("maxNoDays", maximumDays);
		map.addAttribute("stayPeriods", stayPeriods);
		return HTMLPage.APPLY_STAY_EXTENSION_REQUEST;
	}

	private void getGuestDetails(ModelMap map, Long parentReqId, GuestAccommodationRequestDto guestRequestDetails, List<GuestFilesInformationDto> guestFileInfo) {
		List<StudentBioDataFamilyInfoDto> familyFileInfo = null;
		List<GuestAccommodationGuestDetailsDto> dto = guestAccommodationGuestDetailsService.getGuestId(parentReqId);
		if (dto != null && !dto.isEmpty()) {
			List<Long> guestIds = dto.stream().map(GuestAccommodationGuestDetailsDto::getGuestId)
					.collect(Collectors.toList());
			familyFileInfo = studentBioDataFamilyInfoService.getFileUploadDetails(guestIds, Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
		}
		if (familyFileInfo != null && !familyFileInfo.isEmpty()) {
		    guestRequestDetails.setFamilyDetails(familyFileInfo);
		} else {
		    guestRequestDetails.setUploadFileList(guestFileInfo);
		}
		map.addAttribute("guestRequestDto", guestRequestDetails);
	}

	@GetMapping("${url.widget}")
	public String getGuestAccommodationRequestWidget(ModelMap map, HttpServletRequest request) throws Exception {
		GuestAccommodationRequestDto dto  = guestAccommodationRequestService.getLatestRequest(SecurityCtxUtil.userId().toUpperCase());
	    map.addAttribute("guestRequestDto", dto);
	    return HTMLPage.GUEST_ACCOMMODATION_REQUEST_WIDGET;
	}
	
	@GetMapping("${url.guest.cancel}" + "${id}")
	public @ResponseBody String cancelGuestRequestById(@PathVariable String id, ModelMap map, HttpServletRequest request,RedirectAttributes redirectAttrs)
			throws Exception {
		Long requestId = 0L;
		Long parentReqId = 0L;
		String decodedReference = null;
		if (id != null) {
			decodedReference = MCrypt.getInstance().decryptToString(id);
			String[] parts = decodedReference.split(Constants.BACKTICK);
			requestId = parts.length > 1 && parts[1] != null && !parts[1].isEmpty()
					&& !"null".equalsIgnoreCase(parts[1]) ? Long.parseLong(parts[1]) : 0L;
			parentReqId = parts.length > 2 && parts[2] != null && !parts[2].isEmpty()
					&& !"null".equalsIgnoreCase(parts[2]) ? Long.parseLong(parts[2]) : 0L;
		}
		requestId = (requestId != null && requestId == 0 ) ? parentReqId : requestId;
		String status = guestAccommodationRequestService.cancelGuestAccommodationRequestById(requestId);
		return status;

	}
	
	@PostMapping("${url.check.file.name}")
	public ResponseEntity<Map<String, Boolean>> checkBioId(@RequestBody Map<String, String> request) {
	    String bioId = request.get("bioId");
	    boolean exists = studentBioDataFamilyInfoService.checkFileById(bioId);
	    return ResponseEntity.ok(Collections.singletonMap("exists", exists));
	}
	
	@GetMapping("${url.check.hostel.room.occupancy}")
	public @ResponseBody String checkHostelRoomOccupancy(@RequestParam String checkInDate, @RequestParam String checkOutDate) {
		LocalDate checkIn = LocalDate.parse(checkInDate);
		LocalDate checkOut = LocalDate.parse(checkOutDate);
		return guestAccommodationRequestService.checkHostelRoomOccupancy(SecurityCtxUtil.hostelId(), SecurityCtxUtil.roomId(), checkIn, checkOut);
	}


}
