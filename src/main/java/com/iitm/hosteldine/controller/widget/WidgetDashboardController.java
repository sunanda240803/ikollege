package com.iitm.hosteldine.controller.widget;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.hostel.HostelRoomAllotmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.DashboardWidgetMasterDto;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentHostelRoomVacatingRequestDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.exception.GlobalExceptionHandler;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.OtherCandidate.OtherCandidateService;
import com.iitm.hosteldine.service.dashboard.DashboardService;
import com.iitm.hosteldine.service.dashboard.DashboardWidgetPrivilegeService;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.dashboard}")
public class WidgetDashboardController {

	private final CommonResponseUtil commonResponseUtil;
	private final DashboardService dashboardService;
	private final StudentBioDataService studentBioDataService;
	private final DashboardWidgetPrivilegeService dashboardWidgetPrivilegeService;
	private final OtherCandidateService otherCandidateService;
	private final DeanDashboardService deanDashboardService;
	private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;
    private final HostelRoomAllotmentService hostelRoomAllotmentService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final FileService fileService;

    @Value("${url.dashboard}" + "${url.student}")
	private String getStudentDashboard;

	@Value("${url.other.candidate.user}")
	private String otherCandidate;

	@Value("${url.accommodation.request}")
	private String accommodationRequest;
	
	@Value("${url.error}")
    private String errorPage;

	@Value("${url.widget}")
	private String widget;

	@Value("${url.hostel.room.vacating.form}")
	private String hostelRoomVacatingFormUrl;

	@Value("${url.ledger.report}")
	private String ledgerReportUrl;

	@Value("${url.hostel.payment}")
	private String hostelPaymentUrl;

	@Value("${url.scholars.stay.extension}")
	private String scholarStayExtensionUrl;

	@GetMapping("${url.student}")
	public String getStudentDashboard(ModelMap model, HttpServletRequest request) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		userDetails.setProfileName(SecurityCtxUtil.userName());

	    List<MenuListDto> menuEntryList = dashboardService.getDashboardList();

	    if (menuEntryList != null) {
		    Optional<MenuListDto> dashboardOptional = menuEntryList.stream()
		            .filter(it -> getStudentDashboard.equals(it.getUrlPath()))
		            .findFirst();
	
		    dashboardOptional.ifPresent(dashboard -> getWidgetByDashboard(request, dashboard));
		    dashboardOptional.ifPresent(dashboard -> addDashboardWidgetsToModel(Optional.of(dashboard), model));
	    }
	    model.addAttribute("bioDataFilledStatus", studentBioDataService.isStudentDataFilled(SecurityCtxUtil.userId().toUpperCase()));
		StudentBioDataFormDetailDto bioDataFormDetailDto = studentBioDataService.getStudentDetails(SecurityCtxUtil.userId().toUpperCase());
	    model.addAttribute("bioDataID", bioDataFormDetailDto.getId() != null ? bioDataFormDetailDto.getId() : null);
		String profileFileName = studentBioDataService.getFileName(bioDataFormDetailDto, bioDataFormDetailDto.getStudentId(), StudentBioDataFormDetailDto::getImageLocation, ModelConstants.FILE_STUDENT_PROFILE);
		bioDataFormDetailDto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName));
		if (bioDataFormDetailDto.getImageBytes() != null) {
			String encodedImage = Base64.getEncoder().encodeToString(bioDataFormDetailDto.getImageBytes());
			model.addAttribute("studentProfile", encodedImage);
		}
	    commonResponseUtil.updateCommonModelAttributes(model, request);
	    return HTMLPage.STUDENT_DASHBOARD;
	}

	@GetMapping("${url.module}")
	public String getModules(ModelMap model, HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		userDetails.setProfileName(SecurityCtxUtil.userName());
		commonResponseUtil.updateCommonModelAttributes(model, request);
		return HTMLPage.DASHBOARD;
	}

	@GetMapping("${url.dean}")
	public String getDean() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		userDetails.setProfileName(SecurityCtxUtil.userName());
		DeanApprovalDto deanApprovalTabsDto = deanDashboardService.getDeanTabs();
		if (deanApprovalTabsDto != null && deanApprovalTabsDto.getPropertyList() != null && !deanApprovalTabsDto.getPropertyList().isEmpty()) {
			PropertyDto propertyDto = deanApprovalTabsDto.getPropertyList().getFirst();
			return Constants.REDIRECT + "/" + propertyDto.getUrl();
		} else {
			return Constants.REDIRECT + errorPage + GlobalExceptionHandler.NO_DASHBOARD_PARAM;
		}
	}

	@GetMapping("${url.other}")
	public String getOthers(ModelMap model, HttpServletRequest request) throws Exception {
		commonResponseUtil.updateCommonModelAttributes(model, request);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

		CandidateProfileDto profileDto = otherCandidateService.getOtherCandidateDetails(Long.parseLong(userDetails.getUserId()));

		if (profileDto != null) {
			userDetails.setProfileImageName(profileDto.getId() + "_profile");
			userDetails.setProfileName(profileDto.getCandiateFullName());
			if(Objects.nonNull(profileDto.getId()) && !profileDto.getId().equals(SecurityCtxUtil.candidateId())){
				String candidateId = String.valueOf(profileDto.getId());
				Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
						.map(Authentication::getPrincipal)
						.filter(MyUserDetails.class::isInstance)
						.map(MyUserDetails.class::cast)
						.ifPresent(details->details.setCandidateId(String.valueOf(candidateId)));
			}
			model.addAttribute("imageName", userDetails.getProfileImageName());
			if ((profileDto.getId() != null && profileDto.getId() > 0) && !profileDto.getAccommodationRequest()) {
				model.addAttribute("userId", MCrypt.getInstance().encryptToText(userDetails.getUserId()));
				model.addAttribute("requestKey",
						MCrypt.getInstance().encryptToText(ModelConstants.REQUEST_KEY + Utility.getCurrentTimeStamp()));
				return HTMLPage.OTHER_DASHBOARD;
			} else if ((profileDto.getId() != null && profileDto.getId() > 0)) {
				return Constants.REDIRECT + accommodationRequest;
			} else {
				String encryptedRequestId = MCrypt.getInstance().encryptToText(userDetails.getUserId());
				return Constants.REDIRECT + otherCandidate + "/" + encryptedRequestId;
			}
		}
		return HTMLPage.OTHER_DASHBOARD;
	}

	private void getWidgetByDashboard(HttpServletRequest request, MenuListDto dashboard) {
		try {
			dashboardWidgetPrivilegeService.getWidgetListByMenu(dashboard) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unused")
	private void addDashboardWidgetsToModel(Optional<MenuListDto> menuEntry, ModelMap model) {
		if (menuEntry != null && menuEntry.isPresent()) {
			ArrayList<DashboardWidgetMasterDto> widgetList = menuEntry.get().getWidgetList();
			Set<String> allowedUrls = Set.of( hostelRoomVacatingFormUrl, hostelPaymentUrl);
			StudentHostelRoomVacatingRequestDto dto = studentHostelRoomVacatingRequestService.getStudentDetails();
			StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(SecurityCtxUtil.userId().toUpperCase());
			boolean isApproved = WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(dto.getHostelOrWardenApprovalStatus());
			LocalDate vacatingDate = dto.getVacatingDate();
			boolean isDayScholarUser = ModelConstants.YES.equalsIgnoreCase(studentDetailsInfoDto.getDayScholar());
			boolean isPastDate = (vacatingDate == null) || vacatingDate.isBefore(LocalDate.now());
			List<DashboardWidgetMasterDto> lgWidgets = widgetList.stream()
					.filter(widget -> ModelConstants.LARGE.equalsIgnoreCase(widget.getWidgetSize()))
					.filter(widget -> !(isMsOrPhdStudent() && scholarStayExtensionUrl.equals(widget.getWidgetUrl())))
					.filter(widget -> (isApproved && isPastDate) || isDayScholarUser ? allowedUrls.contains(widget.getWidgetUrl()) : true)
					.sorted(Comparator.comparingInt(DashboardWidgetMasterDto::getOrderBy))
					.collect(Collectors.toList());

			List<DashboardWidgetMasterDto> smWidgets = widgetList.stream()
					.filter(widget -> ModelConstants.SMALL.equalsIgnoreCase(widget.getWidgetSize()))
					.filter(widget -> !(isMsOrPhdStudent() && scholarStayExtensionUrl.equals(widget.getWidgetUrl())))
					.filter(widget -> (isApproved && isPastDate) || isDayScholarUser ? allowedUrls.contains(widget.getWidgetUrl()) : true)
					.sorted(Comparator.comparingInt(DashboardWidgetMasterDto::getOrderBy))
					.collect(Collectors.toList());
			model.addAttribute("lgWidgets", lgWidgets);
			model.addAttribute("smWidgets", smWidgets);
		}
	}

	private boolean isMsOrPhdStudent() {
		String studentType = Objects.requireNonNull(SecurityCtxUtil.userName()).toUpperCase().substring(4, 5);
		return studentType.equalsIgnoreCase(
				commonResponseUtil.getMessage(StudentConstants.D.getStudentConstant())
		) || studentType.equalsIgnoreCase(
				commonResponseUtil.getMessage(StudentConstants.S.getStudentConstant())
		);
	}

    @GetMapping("${url.student}" + "${url.pdf.download}")
    public ResponseEntity<Resource> downloadStudentBioDataPDF() throws Exception {
        Resource resource = studentBioDataService.getStudentBioDataPDF(SecurityCtxUtil.userId().toUpperCase(),true);
		return Utility.prepareDownloadFile(resource);
    }

    @GetMapping("${url.student}" + "${url.inventory.list}")
    public String getInventoryDetails(Model model) {
        String studentId = SecurityCtxUtil.userId().toUpperCase();
        System.out.println("Dynamically fetched Student ID: " + studentId);
        List<RoomInventoryForm> inventoryList = hostelRoomAllotmentService.getInventoryDetailsStuid(studentId);
        model.addAttribute("inventoryList", inventoryList);
        return HTMLPage.VIEW_INVENTORY;
    }
}
