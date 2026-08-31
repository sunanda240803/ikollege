package com.iitm.hosteldine.service.dean;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestDto;
import com.iitm.hosteldine.dto.dean.*;
import com.iitm.hosteldine.form.StayExtensionRequestForm;
import com.iitm.hosteldine.service.dashboard.student.StudentWorkflowService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.dto.hostel.VacationHostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.StudentBlackListDetailMapper;
import com.iitm.hosteldine.mapper.dashboard.student.StudentWorkflowMapper;
import com.iitm.hosteldine.mapper.hostel.HostelFloorMasterMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInfoMapper;
import com.iitm.hosteldine.mapper.hostel.VacationHostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.mapper.student.AllStudentsDetailsViewMapper;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.model.dean.DashboardTabMasterEntity;
import com.iitm.hosteldine.model.dean.StudentAppointmentRequestHistory;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentAppointmentRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentWorkflowRepository;
import com.iitm.hosteldine.repository.dean.DashboardTabMasterRepository;
import com.iitm.hosteldine.repository.dean.StudentAppointmentRequestHistoryRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.hostel.VacationHostelRoomAllotmentInfoRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.StudentAppointmentRequestService;
import com.iitm.hosteldine.service.hostel.HostelAccommodationService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAccommodationRequestService {
	
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;
	private final ExcelUtility excelUtility;
	private final StudentWithRemarksService studentWithRemarksService;
	private final StudentAppointmentRequestService studentAppointmentRequestService;
	private final StudentWorkflowRepository studentWorkflowRepository;

	private final List<String> includedStatus = List.of(
			WorkflowStatus.PENDING.getStatus(),
			WorkflowStatus.DEFAULT.getStatus(),
			WorkflowStatus.CANCELLED.getStatus(),
			WorkflowStatus.DELETED.getStatus(),
			WorkflowStatus.REJECTED.getStatus()
	);

	private final StudentAppointmentRequestRepository studentAppointmentRequestRepository;
	private final HostelAccommodationService hostelAccommodationService;
	private final DashboardTabMasterRepository dashboardTabMasterRepository;
	private final StudentAppointmentRequestHistoryRepository studentAppointmentRequestHistoryRepository;
	private final MailTemplateRepository mailTemplateRepository;
	private final HostelMasterService hostelMasterService;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;
	private final HostelRoomInfoRepository hostelRoomInfoRepository;
	private final VacationHostelRoomAllotmentInfoRepository vacationHostelRoomAllotmentInfoRepository;
	private final CommonResponseUtil commonResponseUtil;
	private final StudentWorkflowService studentWorkflowService;

	@Value("${url.dean.student.accommodation.request}")
	public String studentAccommodationUrl;

	@Value("${url.dean.scholars}")
	public String scholarsUrl;

	@Value("${url.dean.student.stay.extension}")
	public String extensionUrl;

	public String getReportHeaderName(String url) {
		if (url.equals(studentAccommodationUrl)) {
			return messageSource.getMessage("message.label.accommodation.report", null, Locale.getDefault());
		} else if (url.equals(scholarsUrl)) {
			return messageSource.getMessage("message.label.scholars.report", null, Locale.getDefault());
		} else {
			return messageSource.getMessage("message.label.stay.extension.report", null, Locale.getDefault());
		}
	}

	public List<DeanAccommodationRequestDto> getStudentAccommodationRequestList(PaginationForm form, String url,
			FilterCriteriaDto filter, DeanApprovalDto columnDto) {

		Object[] entityList = fetchStudentAccommodationRequestData(form, url, filter);
		List<HostelMasterDto> maleHostelList;
		List<HostelMasterDto> femaleHostelList;
		if(RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
			List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
			maleHostelList = getMaleHostelList(hostelList);
			femaleHostelList = getFemaleHostelList(hostelList);
		} else{
			maleHostelList = Collections.emptyList();
			femaleHostelList = Collections.emptyList();
		}

		return Arrays.stream(entityList).map(data -> {
			Object[] objects = (Object[]) data;
			DeanAccommodationRequestDto dto = new DeanAccommodationRequestDto();
			List<PropertyDto> actionList = new ArrayList<PropertyDto>();

			try {
				String requestStatus = String.valueOf(objects[0]);
				String reqWorkFlowStatus = String.valueOf(objects[1]);
				String studentIdVal = String.valueOf(objects[5]);
				String allottedHostelName = getStringValue(objects[22]);
				String allottedRoomNo = getStringValue(objects[23]);
				String allottedSeatName = getStringValue(objects[24]);
				String checkInAllowedStatus = getStringValue(objects[32]);
				Long requestId = (Long) objects[2];
				String approveStatus = requestStatus;
				for (PropertyDto action : columnDto.getActionUrlList()) {
					if(action.getAction() != null && action.getAction()) {
						if (SecurityCtxUtil.userRole().equals(RoleEnum.DEAN.getValue())) {
							if (action.getPropertyValue().equals(WorkflowStatus.VIEW.getStatus())) {
								String viewURL = "/view?data=";
								PropertyDto actionDto = new PropertyDto();
								actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
								actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
								actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
								if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DEFAULT.getStatus())
										&& requestStatus != null && !requestStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
									actionDto.setUrl(url + viewURL + encryptAccommodationRequestUrl(studentIdVal, requestId, "v"));
									approveStatus = WorkflowStatus.VALIDATING.getStatus();
								} else if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus())) {
									actionDto.setUrl(url + viewURL + encryptAccommodationRequestUrl(studentIdVal, requestId, "a"));
									approveStatus = messageSource.getMessage("message.button.approve", null, Locale.getDefault());
								} else if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
									actionDto.setUrl(url + viewURL + encryptAccommodationRequestUrl(studentIdVal, requestId, "c"));
									approveStatus = messageSource.getMessage("message.label.approve.complete", null, Locale.getDefault());
								} else if ((reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus()))
										|| (requestStatus != null && requestStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus()))) {
									actionDto.setUrl(url + viewURL + encryptAccommodationRequestUrl(studentIdVal, requestId, "r"));
									approveStatus = WorkflowStatus.REJECTED.getStatus();
								} else if (reqWorkFlowStatus != null
										&& (reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.CANCELLED.getStatus())
												|| reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DELETED.getStatus())
												|| reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus()))) {
									if (reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.CANCELLED.getStatus())) {
										approveStatus = WorkflowStatus.CANCELLED.getStatus();
									} else if (reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DELETED.getStatus())) {
										approveStatus = WorkflowStatus.DELETED.getStatus();
									} else if (reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.CANCELLED_AFTER_APPROVED.getStatus())) {
										approveStatus = messageSource.getMessage("message.label.cancelled.after.approval", null, Locale.getDefault());
									}
									actionDto.setUrl(url + viewURL + encryptAccommodationRequestUrl(studentIdVal, requestId, "d"));
								}

								actionList.add(actionDto);
							}
							if (action.getPropertyValue().equals(WorkflowStatus.RESEND_MAIL.getStatus())) {
								if (reqWorkFlowStatus != null
										&& reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DEFAULT.getStatus())
										&& requestStatus != null
										&& !requestStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
									try {
										PropertyDto actionDto = new PropertyDto();
										actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
										actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
										actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
										actionDto.setUrl(url + "/resendMail?data=" + encryptAccommodationRequestUrl(studentIdVal, requestId, null));
										actionList.add(actionDto);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							if (action.getPropertyValue()
									.equals(messageSource.getMessage("message.label.pdf", null, Locale.getDefault()))) {
								if (requestStatus != null && (requestStatus
										.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())
										|| requestStatus.equalsIgnoreCase(WorkflowStatus.ALLOTTED.getStatus()))) {
									String pdfURL = "/pdfDownload?data=";
									PropertyDto actionDto = new PropertyDto();
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + pdfURL
											+ encryptAccommodationRequestUrl(studentIdVal, requestId, null));
									actionList.add(actionDto);
								}
							}

							if (action.getPropertyValue().equals(WorkflowStatus.CHECKBOX.getStatus())) {
								if (requestStatus != null && (requestStatus
										.equalsIgnoreCase(WorkflowStatus.VALIDATING.getStatus())
										|| requestStatus.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus()))) {
									String bulkApproveURL = commonResponseUtil.getMessage("url.bulk.approve.reject") + "/";
									PropertyDto actionDto = new PropertyDto();
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + bulkApproveURL
											+ encryptAccommodationRequestUrl(studentIdVal, requestId, null));
									actionList.add(actionDto);
								}
							}
						}

						if (SecurityCtxUtil.userRole().equals(RoleEnum.CCW_OFFICE.getValue())) {
							PropertyDto actionDto = new PropertyDto();
							if (action.getPropertyValue().equals(WorkflowStatus.VIEW.getStatus())) {
								actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
								actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
								actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
								actionDto.setUrl(url + "/view?data=" + encryptAccommodationRequestUrl(studentIdVal, requestId, null));
								actionList.add(actionDto);
							} else if (action.getPropertyValue().equals(WorkflowStatus.ALLOCATE.getStatus())) {
								try {
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + commonResponseUtil.getMessage("url.allocate") + ModelConstants.SLASH + encryptAccommodationRequestUrl(studentIdVal, requestId, null));
									actionList.add(actionDto);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (action.getPropertyValue().equals(WorkflowStatus.CHANGE.getStatus())
									&& WorkflowStatus.ALLOTTED.getStatus().equals(requestStatus)) {
								try {
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									String originalStyle = action.getActionStyle() != null ? action.getActionStyle() : "";
									String updatedStyle = originalStyle.replace("d-none", "").trim();
									actionDto.setActionStyle(updatedStyle);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionList.add(actionDto);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						if (SecurityCtxUtil.userRole().equals(RoleEnum.DEAN.getValue())
								|| SecurityCtxUtil.userRole().equals(RoleEnum.CCW_OFFICE.getValue())) {
							if (action.getPropertyValue().equals(WorkflowStatus.DELETE.getStatus())) {
								if ((reqWorkFlowStatus != null
										&& reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DEFAULT.getStatus())
										&& requestStatus != null
										&& !requestStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus()))
										|| (reqWorkFlowStatus != null
												&& reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus()))) {
									String deleteURL = "/delete?data=";
									PropertyDto actionDto = new PropertyDto();
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + deleteURL + encryptAccommodationRequestUrl(studentIdVal, requestId, null));
									actionList.add(actionDto);
								}
							}
						}
						
						if (SecurityCtxUtil.userRole().equals(RoleEnum.HOSTEL_CHECK_IN.getValue())
								|| SecurityCtxUtil.userRole().equals(RoleEnum.WARDEN.getValue())) {
							PropertyDto actionDto = new PropertyDto();
							if (action.getPropertyValue().equals(WorkflowStatus.VIEW.getStatus())) {
								actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
								actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
								actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
								actionDto.setUrl(url + "/view?data=" + encryptAccommodationRequestUrl(studentIdVal, requestId, null));
								actionList.add(actionDto);
							}
							if (action.getPropertyValue().equals(WorkflowStatus.CHECK_IN.getStatus())) {
								if(requestStatus != null && requestStatus.equalsIgnoreCase(WorkflowStatus.ALLOTTED.getStatus())
										&& checkInAllowedStatus.equalsIgnoreCase(Constants.TRUE)) {
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + "/updateCheckInOrCheckOut?data=" + encryptAccommodationRequestUrl(studentIdVal, requestId, null)+"&status="+WorkflowStatus.CHECKED_IN.getStatus());
									actionList.add(actionDto);
								}
								
							}
							if (action.getPropertyValue().equals(WorkflowStatus.CHECK_OUT.getStatus())) {
								if(requestStatus != null && requestStatus.equalsIgnoreCase(WorkflowStatus.CHECKED_IN.getStatus())
										&& checkInAllowedStatus.equalsIgnoreCase(Constants.TRUE)) {
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(action.getPropertyValue() != null ? action.getPropertyValue() : null);
									actionDto.setUrl(url + "/updateCheckInOrCheckOut?data=" + encryptAccommodationRequestUrl(studentIdVal, requestId, null)+"&status="+WorkflowStatus.CHECKED_OUT.getStatus());
									actionList.add(actionDto);
								}
							}
						}
					}
				}
				if (Constants.MALE.equalsIgnoreCase(String.valueOf(objects[8])) ||
						Constants.MALE_FULL_FORM.equalsIgnoreCase(String.valueOf(objects[8]))) {
					dto.setHostelListGenderBased(maleHostelList);
				}else {
					dto.setHostelListGenderBased(femaleHostelList);
				}

				if (SecurityCtxUtil.userRole().equals(RoleEnum.HOSTEL_CHECK_IN.getValue())
						|| SecurityCtxUtil.userRole().equals(RoleEnum.WARDEN.getValue())) {
					if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.PENDING.getStatus())) {
						approveStatus = WorkflowStatus.PENDING.getStatus();
					}
					if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.DEFAULT.getStatus())
							&& requestStatus != null && !requestStatus.equalsIgnoreCase(WorkflowStatus.REJECTED.getStatus())) {
						approveStatus = WorkflowStatus.VALIDATING.getStatus();
					}
					if (reqWorkFlowStatus != null && reqWorkFlowStatus.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())
							&& requestStatus != null && requestStatus.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
						approveStatus = WorkflowStatus.APPROVED.getStatus();
					}
					Set<String> appStatusSet = Set.of(
							WorkflowStatus.ALLOTTED.getStatus(), 
							WorkflowStatus.REJECTED.getStatus(), 
							WorkflowStatus.CANCELLED.getStatus(),
							WorkflowStatus.DELETED.getStatus(),
							WorkflowStatus.CHECKED_IN.getStatus(),
							WorkflowStatus.CHECKED_OUT.getStatus()
						);
					if (appStatusSet.contains(requestStatus.trim())) {
						approveStatus = requestStatus;
					}
				}
				
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
				dto.setApproveStatus(approveStatus);
				dto.setSlNo(requestId);
				dto.setApplicationId(studentIdVal);
				dto.setCreatedAt(
						objects[6] != null ? ((java.sql.Date) objects[6]).toLocalDate().format(dateFormatter) : null);
				dto.setGender(objects[8] != null
						? (String.valueOf(objects[8]).equalsIgnoreCase(Constants.MALE) ? Constants.MALE_FULL_FORM
						: Constants.FEMALE_FULL_FORM) : null);
				dto.setAppointmentFrom(ValidationCommon.formatDate(objects[11], dateFormatter) != null
						? ValidationCommon.formatDate(objects[11], dateFormatter) : Constants.HYPHEN);
				dto.setAppointmentTo(ValidationCommon.formatDate(objects[12], dateFormatter) != null
						? ValidationCommon.formatDate(objects[12], dateFormatter) : Constants.HYPHEN);
				dto.setStayFrom(ValidationCommon.formatDate(objects[13], dateFormatter) != null
						? ValidationCommon.formatDate(objects[13], dateFormatter) : Constants.HYPHEN);
				dto.setStayTo(ValidationCommon.formatDate(objects[14], dateFormatter) != null
						? ValidationCommon.formatDate(objects[14], dateFormatter) : Constants.HYPHEN);
				dto.setApprovalDate(ValidationCommon.formatDate(objects[21], dateFormatter) != null
						? ValidationCommon.formatDate(objects[21], dateFormatter) : Constants.HYPHEN);
				dto.setDining(objects[3] != null ? getDiningStatus(String.valueOf(objects[3])) : null);
				dto.setGrossPay(objects[15] != null ? ((Double) objects[15]) : null);
				dto.setFirstName(getStringValue(objects[7]));
				dto.setValidatingAuthority(getStringValue(objects[16]));
				dto.setValidatingAuthorityEmail(getStringValue(objects[17]));
				dto.setCity(getStringValue(objects[34]));
				dto.setPhoneNumber(getStringValue(objects[36]));
				dto.setOccupancy(getStringValue(objects[37]));
				dto.setState(getStringValue(objects[35]));
				dto.setPurpose(getStringValue(objects[38]));
				dto.setCategoryOthers(getStringValue(objects[41]));
				dto.setDiningOthers(getStringValue(objects[4]));
				dto.setHodName(getStringValue(objects[39]));
				dto.setHodEmail(getStringValue(objects[40]));
				dto.setCancelDescription(getStringValue(objects[42]));
				dto.setAuthorityType(getStringValue(objects[43]));
				dto.setApprovalLevel(getStringValue(objects[44]));
				dto.setRejectionReason(getStringValue(objects[19]));
				dto.setApprovalNotes(getStringValue(objects[18]));
				dto.setCategory(getCategoryKey(objects[20]));
				dto.setAdmissionDate(ValidationCommon.formatDate(objects[28], dateFormatter) != null
						? ValidationCommon.formatDate(objects[28], dateFormatter) : Constants.HYPHEN);
				dto.setThesisSubmittedDate(ValidationCommon.formatDate(objects[27], dateFormatter) != null
						? ValidationCommon.formatDate(objects[27], dateFormatter) : Constants.HYPHEN);
				dto.setHostelName(getStringValue(objects[29]));
				dto.setAllottedHostelName(allottedHostelName);
				dto.setRoomNo(allottedRoomNo);
				dto.setSeat(getStringValue(allottedSeatName));
				dto.setActionList(actionList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dto;
	    }).toList();
	}

	private List<HostelMasterDto> getFemaleHostelList(List<HostelMasterDto> hostelList) {
		return hostelList.stream()
				.filter(hostel -> hostel.getHostelGenderType() != null &&
						(hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE) ||
								hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE_FULL_FORM)))
				.toList();
	}

	private List<HostelMasterDto> getMaleHostelList(List<HostelMasterDto> hostelList) {
		return hostelList.stream()
				.filter(hostel -> hostel.getHostelGenderType() != null &&
						(hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE) ||
								hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE_FULL_FORM)))
				.toList();
	}

	private Object[] fetchStudentAccommodationRequestData(PaginationForm form, String url,
			FilterCriteriaDto filter) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());

		return allStudentsDetailsViewRepository.getAccommodationRequestList(filter.getValidationStatus(),
				filter.getCategory(), filter.getAppointmentFromDate(), filter.getAppointmentToDate(),
				filter.getStayFromDate(), filter.getStayToDate(), filter.getStudentName(), filter.getStudentId(), null,
				null, SecurityCtxUtil.userRole(), filter.getSubmittedFromDate(), filter.getSubmittedToDate(),
				filter.getApprovalFromDate(), filter.getApprovalToDate(), filter.getHostelId(),
				SecurityCtxUtil.userName(), Integer.parseInt(filter.getTabNo()), filter.getCurrentDayStayFlag());
	}

	private List<Object[]> fetchAccommodationRequestData(PaginationForm form, String url, FilterCriteriaDto filter) {
		return allStudentsDetailsViewRepository.getAccommodationRequestDownloadList(filter.getValidationStatus(),
				filter.getCategory(), filter.getAppointmentFromDate(), filter.getAppointmentToDate(),
				filter.getStayFromDate(), filter.getStayToDate(), filter.getStudentName(), filter.getStudentId(), null,
				null, SecurityCtxUtil.userRole(), filter.getSubmittedFromDate(), filter.getSubmittedToDate(),
				filter.getApprovalFromDate(), filter.getApprovalToDate(), filter.getHostelId(),
				SecurityCtxUtil.userName(), Integer.parseInt(filter.getTabNo()), null);
	}

	public Workbook downloadStudentAccommodationRequestReport(PaginationForm form, String url, FilterCriteriaDto filter)
			throws Exception {
		String reportName = getReportHeaderName(url);
		String sheetName = reportName;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.ACCOMMODATION_REQUEST_REPORT_HEADER_DATA.length;
		String[] headerData = ExcelConstants.ACCOMMODATION_REQUEST_REPORT_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.ACCOMMODATION_REQUEST_REPORT_HEADER_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		// Add new row with title "Office of the Hostel Management - IITMADRAS CAMPUS"
		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
				Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		// Row 1 with wellness report title
		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue(reportName);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		// Row 2 with report date
		Row row2 = sheet.createRow(2);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1 = row2.createCell(0);
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));

		// Row 3 for header data
		Row row3 = sheet.createRow(3);
		excelUtility.createHeader(row3, 0, headerData, workbook);

		// Set column widths
		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		// Fetch and populate data
		List<Object[]> entityList = fetchAccommodationRequestData(form, url, filter);
		AtomicInteger rowCount = new AtomicInteger(3); // Start from row 4 (index 3)

		entityList.forEach(accommodation -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());
			try {
				String gender = Strings.EMPTY;
				if (accommodation[8] != null) {
					gender = getNonNullValue(accommodation[8]).equals(Constants.MALE) ? Constants.MALE_FULL_FORM
							: Constants.FEMALE_FULL_FORM;
				}
				
				String[] values = { getNonNullValue(accommodation[2]),
						getNonNullValue(DateUtility.formatDate(accommodation[6])), getNonNullValue(accommodation[7]),
						getNonNullValue(accommodation[5]), getNonNullValue(gender),
						getNonNullValue(DateUtility.formatDate(accommodation[9])), getNonNullValue(accommodation[10]),
						getNonNullValue(DateUtility.formatDate(accommodation[11])),
						getNonNullValue(DateUtility.formatDate(accommodation[12])),
						getNonNullValue(DateUtility.formatDate(accommodation[13])),
						getNonNullValue(DateUtility.formatDate(accommodation[14])),
						getNonNullValue(getCategoryKey(accommodation[20])), Strings.EMPTY, Strings.EMPTY,
						getNonNullValue(accommodation[16]), getNonNullValue(accommodation[17]),
						getNonNullValue(accommodation[37]), getNonNullValue(accommodation[1]),
						getNonNullValue(accommodation[18]), getNonNullValue(accommodation[22]),
						getNonNullValue(accommodation[23]), getNonNullValue(accommodation[24]) };
				
				for (int colIdx = 0; colIdx < values.length; colIdx++) {
					excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Auto-size columns
		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
		return workbook;
	}

	@Transactional
	public boolean deleteAccommodationRequest(String encryptedKey) throws Exception {
		String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
		String user = SecurityCtxUtil.userId();
		Long requestId = Long.parseLong(split[1]);
		String studentId = getNonNullValue(split[0]);
		LocalDateTime now = DateUtility.getNowTimeInstant();
		try {
			int updateCount1 = studentWorkflowRepository.updateCancelRequestForAccommodation(user.toUpperCase(), now,
					WorkflowStatus.DELETED.getStatus(), ModelConstants.STATUS_ACTIVE, requestId, studentId,Constants.CCW,RoleEnum.DEAN.getValue());
			if (updateCount1 > 0) {
				String deleteNote = messageSource.getMessage("message.label.your.request.deleted.by", null, Locale.getDefault());
				String facultyName = SecurityCtxUtil.userName();
				String note = deleteNote + ModelConstants.SPACE + facultyName;
				int updateCount2 = studentAppointmentRequestRepository.updateCancelRequestForValidatingAccommodation(
						user.toUpperCase(), now, WorkflowStatus.DELETED.getStatus(), note, requestId, studentId);
				if (updateCount2 == 0) {
					throw new RuntimeException();
				}
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Transactional
	public String resendMail(String data, HttpServletRequest request) throws Exception {
		String[] split = MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK);
		String studentId = getNonNullValue(split[0]);
		Long requestId = Long.parseLong(split[1]);
		return hostelAccommodationService.processResendMail(studentId, requestId, request);
	}

	private String getDiningStatus(String diningValue) {
	    if (diningValue == null) {
	        return null;
	    }
	    switch (diningValue.toLowerCase(Locale.ROOT)) {
	        case "y":
	        case "t":
	            return WorkflowStatus.REQUIRED.getStatus();
	        case "n":
	        case "f":
	            return WorkflowStatus.NOT_REQUIRED.getStatus();
	        case "o":
	            return WorkflowStatus.OTHERS.getStatus();
	        default:
	            return null;
	    }
	}

	// Get category list from NATURE_OF_APPOINTMENT
	private String getCategoryKey(Object categoryId) {
		ArrayList<SimsConfigDataJsonArrayDto> categoryList = simsConfigDataService
				.getSimConfigValueFromJsonArray(SimsConfigDataService.NATURE_OF_APPOINTMENT);
		return categoryList.stream().filter(item -> item.getId().equals(String.valueOf(categoryId)))
				.map(SimsConfigDataJsonArrayDto::getValue).findFirst()
				.orElse(getCategoryFallback(String.valueOf(categoryId)));
	}

	private String getCategoryFallback(String category) {
		if ("scholar".equalsIgnoreCase(category) || "extension".equalsIgnoreCase(category) || "ThesisSubmitted".equalsIgnoreCase(category)) {
			return "Scholar";
		} else if ("stustayextension".equalsIgnoreCase(category)) {
			return "Student Stay Extension";
		}
		return "NA";
	}
    
	public static String encryptAccommodationRequestUrl(String studentId, Long requestId, String status)
			throws Exception {
		String encryptKey = null;
		if (status != null && !status.isEmpty()) {
			encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + status + Constants.BACKTICK
					+ Utility.getCurrentTimeStamp();
		} else {
			encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
		}
		return MCrypt.getInstance().encryptToText(encryptKey);
	}

	public Resource downloadAccommodationDetailsPDF(String encryptedKey) throws Exception {
		String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
		String studentId = getNonNullValue(split[0]);
		Long requestId = Long.parseLong(split[1]);
		return hostelAccommodationService.generatePdf(studentId, requestId);
	}

	public String getStringValue(Object obj) {
		return obj != null && !String.valueOf(obj).trim().isEmpty() ? String.valueOf(obj) : Constants.HYPHEN;
	}

	public String getNonNullValue(Object obj) {
		return (obj != null) ? String.valueOf(obj) : Strings.EMPTY;
	}

	public AllStudentsDetailsViewDto getAllStudentDetails(String studentId){
		return allStudentsDetailsViewRepository
				.findBystudentId(studentId)
				.map(AllStudentsDetailsViewMapper.INSTANCE::fromAllStudentsDetailsViewEntity)
				.orElse(new AllStudentsDetailsViewDto());
	}

	public StudentBlackListDetailDto getBlackListDetails(String studentId) {
		StudentBlackListDetailDto studentBlackListDetailDto = studentWithRemarksService
				.getStudentDateBlacklistDetails(studentId).map(StudentBlackListDetailMapper.INSTANCE::toDto).orElse(new StudentBlackListDetailDto());
		if (Objects.isNull(studentBlackListDetailDto.getFromDate()) || Objects.isNull(studentBlackListDetailDto.getToDate())) {
			studentBlackListDetailDto.setStudentRemark(StudentConstants.NORMAL_STUDENT.getStudentConstant());
		} else {
			studentBlackListDetailDto.setStudentRemark(StudentConstants.BLACK_LIST_STUDENT.getStudentConstant());
		}
		return studentBlackListDetailDto;
	}

	@Transactional
	public String updateApprovalStatus(List<String> split, StudentAppointmentRequestDto studentAppointmentRequestDto, HttpServletRequest request) throws Exception {
		StudentWorkflowDto studentWorkflowDtoByIdAndStatusIn = studentWorkflowRepository
				.findByIdAndActiveFlagAndStatusIn(Long.valueOf(split.getFirst()),ModelConstants.STATUS_ACTIVE, includedStatus)
				.map(StudentWorkflowMapper.INSTANCE::toDto)
				.orElse(null);

		StudentWorkflowDto studentWorkflowDtoByIdAndActiveFlag = studentWorkflowRepository.findByIdAndActiveFlag(Long.valueOf(split.getFirst()), ModelConstants.STATUS_ACTIVE)
				.map(StudentWorkflowMapper.INSTANCE::toDto)
				.orElse(null);

		String result;

		Optional<StudentAppointmentRequestEntity> appointmentRequestNotInDeletedAndCancelled = studentWorkflowRepository
				.getAppointmentDetailsByWorkflowRequestId(
						Long.valueOf(split.getFirst()),
                        LocalDateTime.parse(split.get(1)),
						List.of(WorkflowStatus.DELETE.getStatus(), WorkflowStatus.CANCELLED.getStatus()),
						ModelConstants.STATUS_ACTIVE
				);

		if (isStatusRejected(split)) return messageSource.getMessage("message.label.student.appointment.already.rejected", null, Locale.getDefault());
		if (appointmentRequestNotInDeletedAndCancelled.isEmpty())
			return Constants.INVALID_REQUEST;
		if (isStatusOverrideApprovedOrApproved(split)) {
			if (studentWorkflowDtoByIdAndStatusIn == null)
				result = Constants.INVALID_REQUEST;
			else {
				updateStatus(split, studentAppointmentRequestDto, studentWorkflowDtoByIdAndStatusIn, appointmentRequestNotInDeletedAndCancelled.get(), request);
				result = Constants.UPDATED + Constants.BACKTICK + split.get(2);
			}
		} else {
			if (studentWorkflowDtoByIdAndActiveFlag == null)
				result = Constants.INVALID_REQUEST;
			else {
				updateStatus(split, studentAppointmentRequestDto, studentWorkflowDtoByIdAndActiveFlag, appointmentRequestNotInDeletedAndCancelled.get(), request);
				studentAppointmentRequestDto.setApprovalStatus(split.get(2));
				result = Constants.UPDATED + Constants.BACKTICK + split.get(2);
			}
		}
		return result;
    }

	@Transactional
	public String saveDetails(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentWorkflowDto studentWorkflowDto) {
		return studentAppointmentRequestRepository
				.findByIdAndActiveFlag(studentWorkflowDto.getRequestId(), ModelConstants.STATUS_ACTIVE)
				.map(entity -> {
					saveStudentAppointmentDetailsHistory(studentAppointmentRequestDto, studentWorkflowDto, entity);
					updateStudentAppointmentRequest(studentAppointmentRequestDto, entity);
					return Constants.UPDATED;
				})
				.orElse(Constants.ERROR);
	}

	public void updateStudentAppointmentRequest(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity){
		studentAppointmentRequestEntity.setStayFrom(studentAppointmentRequestDto.getStayFrom());
		studentAppointmentRequestEntity.setStayTo(studentAppointmentRequestDto.getStayTo());
		studentAppointmentRequestEntity.setOccupancy(studentAppointmentRequestDto.getOccupancy());
		studentAppointmentRequestEntity.setModifiedBy(SecurityCtxUtil.userName());
		studentAppointmentRequestEntity.setModifiedAt(LocalDateTime.now());
		studentAppointmentRequestRepository.save(studentAppointmentRequestEntity);
	}

	private void updateStatus(List<String> split, StudentAppointmentRequestDto studentAppointmentRequestDto, StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, HttpServletRequest request) throws Exception {
		List<StudentAppointmentRequestDto> validatorsList = new ArrayList<>();
		boolean isNextValidatorListEmpty;
		Optional<List<Object[]>> updateApprovalStatus;
		String modifiedBy = Objects.nonNull(SecurityCtxUtil.userName()) ? SecurityCtxUtil.userName() : studentWorkflowDto.getValidatorName();
		if (isStatusOverrideApprovedOrApproved(split)){
			updateApprovalStatus = studentWorkflowRepository
					.updateApprovalStatus(Long.valueOf(split.getFirst()), LocalDateTime.parse(split.get(1)),
							split.get(2), null, studentAppointmentRequestDto.getApprovalNotes(), modifiedBy);
		}else {
			updateApprovalStatus = studentWorkflowRepository
					.updateApprovalStatus(Long.valueOf(split.getFirst()), LocalDateTime.parse(split.get(1)),
							split.get(2), split.get(3), studentAppointmentRequestDto.getApprovalNotes(), modifiedBy);
		}

		if (updateApprovalStatus.isPresent() && !updateApprovalStatus.get().isEmpty()){
			validatorsList = convertToDto(updateApprovalStatus.get());
		}
		isNextValidatorListEmpty = validatorsList.isEmpty();
		 if (isNextValidatorListEmpty && isStatusOverrideApprovedOrApproved(split)) {
			validatorsList = setDefaultList(studentWorkflowDto, studentAppointmentRequestEntity, split);
			datesUpdate(studentAppointmentRequestDto, studentAppointmentRequestEntity, studentWorkflowDto);
		} else if (WorkflowStatus.REJECTED.getStatus().equals(split.get(2))) {
			 validatorsList = setRejectedDefaultList(studentWorkflowDto, studentAppointmentRequestEntity, split);
		}
		updateOccupancyStatus(studentAppointmentRequestDto);
		if (isNextValidatorListEmpty) {
			mailToValidatorAndStudent(studentAppointmentRequestDto, studentWorkflowDto, request, validatorsList, split);
		} else{
			mailToNextLevelValidatorAndStudent(studentAppointmentRequestDto, studentWorkflowDto, request, validatorsList, split);
		}
	}

	private List<StudentAppointmentRequestDto> setDefaultList(StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, List<String> split) {
		StudentAppointmentRequestDto defaultValidator = new StudentAppointmentRequestDto();
		defaultValidator.setAuthorityType(ModelConstants.CCW_OFFICE);
		return getStudentAppointmentRequestDto(studentWorkflowDto, studentAppointmentRequestEntity, defaultValidator);
	}

	private List<StudentAppointmentRequestDto> setRejectedDefaultList(StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, List<String> split) {
		StudentAppointmentRequestDto defaultValidator = new StudentAppointmentRequestDto();
		defaultValidator.setAuthorityType(studentWorkflowDto.getAuthorityType());
		return getStudentAppointmentRequestDto(studentWorkflowDto, studentAppointmentRequestEntity, defaultValidator);
	}

	private List<StudentAppointmentRequestDto> getStudentAppointmentRequestDto(StudentWorkflowDto studentWorkflowDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, StudentAppointmentRequestDto defaultValidator) {
		defaultValidator.setValidatingAuthorityEmail(simsConfigDataService.getSimConfigValue(SimsConfigDataService.APPROVAL_MAIL_TO));
		defaultValidator.setRequestId(String.valueOf(studentWorkflowDto.getRequestId()));
		defaultValidator.setModifiedAt(studentWorkflowDto.getModifiedAt());
		defaultValidator.setValidatingAuthority(studentWorkflowDto.getAuthorityType());
		defaultValidator.setStudentId(studentWorkflowDto.getStudentId());
		defaultValidator.setId(studentWorkflowDto.getId());
		defaultValidator.setCreatedAt(String.valueOf(studentWorkflowDto.getCreatedAt()));
		defaultValidator.setStayFrom(studentAppointmentRequestEntity.getStayFrom());
		defaultValidator.setStayTo(studentAppointmentRequestEntity.getStayTo());
		return List.of(defaultValidator);
	}

	private void mailToNextLevelValidatorAndStudent(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentWorkflowDto studentWorkflowDto, HttpServletRequest request, List<StudentAppointmentRequestDto> nextValidatorsList, List<String> split) {
		if (CollectionUtils.isNotEmpty(nextValidatorsList)) {
			nextValidatorsList.forEach(appointmentRequestDto -> {
				try {
					appointmentRequestDto.setThesisSubmittedDate(
							studentAppointmentRequestDto.getThesisSubmittedDate() == null ? studentAppointmentRequestDto.getStayFrom() : studentAppointmentRequestDto.getThesisSubmittedDate()
					);
					if (isStatusOverrideApprovedOrApproved(split)){
						studentWorkflowDto.setId(appointmentRequestDto.getId());
						studentAppointmentRequestService.saveAppointmentMail(studentWorkflowDto, appointmentRequestDto, request);
					}
					studentAppointmentRequestDto.setStudentMailContent(setNextValidatorToStudentApprovalMailContent(appointmentRequestDto, studentWorkflowDto, split));
					studentAppointmentRequestDto.setMailSubject(appointmentRequestDto.getMailSubject());
					studentAppointmentApproveMail(studentAppointmentRequestDto, studentWorkflowDto, request);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	private String setNextValidatorToStudentApprovalMailContent(StudentAppointmentRequestDto appointmentRequestDto, StudentWorkflowDto studentWorkflowDto, List<String> split) {
		return isStatusOverrideApprovedOrApproved(split) ?
				approvedNextLevelValidatorMailContent(appointmentRequestDto, studentWorkflowDto) :
				rejectedValidatorMailContent(appointmentRequestDto, studentWorkflowDto);
	}

	private String approvedNextLevelValidatorMailContent(StudentAppointmentRequestDto appointmentRequestDto, StudentWorkflowDto studentWorkflowDto) {
		Optional<MailTemplateEntity> studentApprovedMailContent = mailTemplateRepository
				.findByMailType(MailTemplateEntity.STUDENT_SCHOLAR_LEVEL_1);
		if (studentApprovedMailContent.isPresent()){
			String approveTemplate = studentApprovedMailContent.get().getMailTemplate();
			appointmentRequestDto.setMailSubject(studentApprovedMailContent.get().getMailSubject());
			approveTemplate = approveTemplate.replaceAll("#%validatorName%#",(studentWorkflowDto.getValidatorName() != null ?
					studentWorkflowDto.getAuthorityType() + " (" + studentWorkflowDto.getValidatorName() + ")" :
					studentWorkflowDto.getAuthorityType()));
			approveTemplate = approveTemplate.replaceAll("#%nextLevelValidator%#", setNextLevelValidatorName(appointmentRequestDto));
			return approveTemplate;
		}
		return ModelConstants.EMPTY_STRING;
	}

    private String setNextLevelValidatorName(StudentAppointmentRequestDto appointmentRequestDto) {
        return appointmentRequestDto.getValidatingAuthority() != null ?
				appointmentRequestDto.getAuthorityType() + "(" + appointmentRequestDto.getValidatingAuthority() + ")" :
				appointmentRequestDto.getAuthorityType();
    }

	private void mailToValidatorAndStudent(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentWorkflowDto studentWorkflowDto, HttpServletRequest request, List<StudentAppointmentRequestDto> nextValidatorsList, List<String> split) {
		if (CollectionUtils.isNotEmpty(nextValidatorsList)) {
			nextValidatorsList.forEach(appointmentRequestDto -> {
				try {
					appointmentRequestDto.setRejectionReason(studentAppointmentRequestDto.getRejectionReason());
					studentWorkflowDto.setAuthorityType(appointmentRequestDto.getAuthorityType());
					appointmentRequestDto.setThesisSubmittedDate(
							studentAppointmentRequestDto.getThesisSubmittedDate() == null ? studentAppointmentRequestDto.getStayFrom() : studentAppointmentRequestDto.getThesisSubmittedDate()
					);
					if (isStatusOverrideApprovedOrApproved(split)){
						studentAppointmentRequestService.saveAppointmentMail(studentWorkflowDto, appointmentRequestDto, request);
					}
					studentAppointmentRequestDto.setStudentMailContent(setValidatorToStudentApprovalMailContent(appointmentRequestDto, studentWorkflowDto, split));
					studentAppointmentRequestDto.setMailSubject(appointmentRequestDto.getMailSubject());
					studentAppointmentApproveMail(studentAppointmentRequestDto, studentWorkflowDto, request);
				} catch (Exception e) {
					log.error("Exception in saveAppointmentMail:", e);
					throw new RuntimeException(e);
				}
			});
		}
	}

	private String setValidatorToStudentApprovalMailContent(StudentAppointmentRequestDto appointmentRequestDto, StudentWorkflowDto studentWorkflowDto, List<String> split) {
		return isStatusOverrideApprovedOrApproved(split) ?
				approvedValidatorMailContent(appointmentRequestDto, studentWorkflowDto, split) :
				rejectedValidatorMailContent(appointmentRequestDto, studentWorkflowDto);
	}

	private String rejectedValidatorMailContent(StudentAppointmentRequestDto appointmentRequestDto, StudentWorkflowDto studentWorkflowDto) {
		Optional<MailTemplateEntity> studentRejectedMailContent = mailTemplateRepository
				.findByMailType(MailTemplateEntity.STUDENT_SCHOLAR_REJECT);
		if (studentRejectedMailContent.isPresent()){
			String rejectTemplate = studentRejectedMailContent.get().getMailTemplate();
			appointmentRequestDto.setMailSubject(studentRejectedMailContent.get().getMailSubject());
			rejectTemplate = rejectTemplate.replaceAll("#%submittedDate%#",
					(appointmentRequestDto.getThesisSubmittedDate() == null ? appointmentRequestDto.getStayFrom() : appointmentRequestDto.getThesisSubmittedDate())
							.format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH)));
			rejectTemplate = rejectTemplate.replaceAll("#%validatorName%#",(studentWorkflowDto.getValidatorName() != null ?
					studentWorkflowDto.getAuthorityType() + " (" + studentWorkflowDto.getValidatorName() + ")" :
					studentWorkflowDto.getAuthorityType()));
			rejectTemplate = rejectTemplate.replaceAll("#%rejectionReason%#", appointmentRequestDto.getRejectionReason());
			appointmentRequestDto.setMailSubject(studentRejectedMailContent.get().getMailSubject());
			return rejectTemplate;
		}
		return ModelConstants.EMPTY_STRING;
	}

	private String approvedValidatorMailContent(StudentAppointmentRequestDto appointmentRequestDto, StudentWorkflowDto studentWorkflowDto, List<String> split) {
		Optional<MailTemplateEntity> studentApprovedMailContent = mailTemplateRepository
				.findByMailType(MailTemplateEntity.STUDENT_SCHOLAR_LEVEL_2);
		if (studentApprovedMailContent.isPresent()){
			String approveTemplate = studentApprovedMailContent.get().getMailTemplate();
			approveTemplate = approveTemplate.replaceAll("#%submittedDate%#", (appointmentRequestDto.getThesisSubmittedDate() == null ? appointmentRequestDto.getStayFrom() : appointmentRequestDto.getThesisSubmittedDate())
					.format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH)));
			approveTemplate = approveTemplate.replaceAll("#%validatorName%#",(studentWorkflowDto.getValidatorName() != null ?
					studentWorkflowDto.getAuthorityType() + " (" + studentWorkflowDto.getValidatorName() + ")" :
					studentWorkflowDto.getAuthorityType()));
			appointmentRequestDto.setMailSubject(studentApprovedMailContent.get().getMailSubject());
			return approveTemplate;
		}
		return ModelConstants.EMPTY_STRING;
	}

	private void studentAppointmentApproveMail(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentWorkflowDto studentWorkflowDto, HttpServletRequest request) throws Exception {
		studentWorkflowDto.setAuthorityType(ModelConstants.STUDENT);
		studentAppointmentRequestService.saveAppointmentMail(studentWorkflowDto, studentAppointmentRequestDto, request);
	}

	private void updateOccupancyStatus(StudentAppointmentRequestDto studentAppointmentRequestDto) {
		if (studentAppointmentRequestDto != null && Objects.nonNull(studentAppointmentRequestDto.getOccupancy()) && !studentAppointmentRequestDto.getOccupancy().isEmpty()){
			studentWorkflowRepository.updateOccupancyStatus(
					studentAppointmentRequestDto.getId(),
					studentAppointmentRequestDto.getStudentId(),
					studentAppointmentRequestDto.getOccupancy(),
					ModelConstants.STATUS_ACTIVE,
					LocalDateTime.now(),
					SecurityCtxUtil.userName()
			);
		}
	}

	private void datesUpdate(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity, StudentWorkflowDto studentWorkflowDto) {
		if (Objects.nonNull(studentAppointmentRequestDto.getStayFrom()) && Objects.nonNull(studentAppointmentRequestDto.getStayTo())) {
			if (equalsStayFromStayTo(studentAppointmentRequestDto, studentAppointmentRequestEntity)) {
				saveStudentAppointmentDetailsHistory(studentAppointmentRequestDto, studentWorkflowDto, studentAppointmentRequestEntity);
				updateStudentAppointmentDetails(studentAppointmentRequestDto);
			} else {
				updateStudentAppointmentRequestApprovalDate(studentAppointmentRequestEntity);
			}
		} else if (studentAppointmentRequestDto.getStayFrom() == null && studentAppointmentRequestDto.getStayTo() == null) {
			updateStudentAppointmentDetailsWhenDatesAreNull(studentAppointmentRequestDto);
		}
	}

	private void updateStudentAppointmentRequestApprovalDate(StudentAppointmentRequestEntity studentAppointmentRequestEntity) {
		studentWorkflowRepository.updateApprovalDate(
				studentAppointmentRequestEntity.getId(),
				studentAppointmentRequestEntity.getStudentId(),
				ModelConstants.STATUS_ACTIVE,
				LocalDate.now(),
				LocalDateTime.now(),
				SecurityCtxUtil.userName()
		);
	}

	private void updateStudentAppointmentDetails(StudentAppointmentRequestDto studentAppointmentRequestDto) {
		studentWorkflowRepository.updateStayRequestDates(
				studentAppointmentRequestDto.getId(),
				studentAppointmentRequestDto.getStudentId(),
				studentAppointmentRequestDto.getStayFrom(),
				studentAppointmentRequestDto.getStayTo(),
				WorkflowStatus.APPROVED.getStatus(),
				ModelConstants.STATUS_ACTIVE,
				LocalDateTime.now(),
				SecurityCtxUtil.userName(),
				LocalDate.now()
		);
	}

	private void updateStudentAppointmentDetailsWhenDatesAreNull(StudentAppointmentRequestDto studentAppointmentRequestDto) {
		studentWorkflowRepository.updateStayRequestDatesWhenDatesAreNull(
				studentAppointmentRequestDto.getId(),
				studentAppointmentRequestDto.getStudentId(),
				WorkflowStatus.APPROVE.getStatus(),
				ModelConstants.STATUS_ACTIVE,
				LocalDateTime.now(),
				SecurityCtxUtil.userName(),
				LocalDate.now());
	}

	public void saveStudentAppointmentDetailsHistory(StudentAppointmentRequestDto studentAppointmentRequestDto,
													 StudentWorkflowDto studentWorkflowDto,
													 StudentAppointmentRequestEntity studentAppointmentRequestEntity) {
		StudentAppointmentRequestHistory studentAppointmentRequestHistory = new StudentAppointmentRequestHistory();
		studentAppointmentRequestHistory.setRequestType(ModelConstants.STUDENT);
		studentAppointmentRequestHistory.setCandidateId(0L);
		studentAppointmentRequestHistory.setRequestId(studentWorkflowDto.getRequestId());
		studentAppointmentRequestHistory.setStudentId(studentWorkflowDto.getStudentId());
		studentAppointmentRequestHistory.setOriginalStayFrom(studentAppointmentRequestEntity.getStayFrom());
		studentAppointmentRequestHistory.setOriginalStayTo(studentAppointmentRequestEntity.getStayTo());
		studentAppointmentRequestHistory.setModifiedStayFrom(studentAppointmentRequestDto.getStayFrom());
		studentAppointmentRequestHistory.setModifiedStayTo(studentAppointmentRequestDto.getStayTo());
		studentAppointmentRequestHistoryRepository.save(studentAppointmentRequestHistory);
	}

	private boolean equalsStayFromStayTo(StudentAppointmentRequestDto studentAppointmentRequestDto, StudentAppointmentRequestEntity studentAppointmentRequestEntity) {
		return studentAppointmentRequestEntity.getStayFrom() == null ||
				!studentAppointmentRequestDto.getStayFrom().equals(studentAppointmentRequestEntity.getStayFrom()) ||
				studentAppointmentRequestEntity.getStayTo() == null ||
				!studentAppointmentRequestDto.getStayTo().equals(studentAppointmentRequestEntity.getStayTo());
	}

	private boolean isStatusOverrideApprovedOrApproved(List<String> split) {
		return WorkflowStatus.APPROVED.getStatus().equals(split.get(2)) || WorkflowStatus.OVERRIDE_APPROVED.getStatus().equals(split.get(2));
	}

	private List<StudentAppointmentRequestDto> convertToDto(List<Object[]> objects) {
		return objects.stream().map(obj -> {
			StudentAppointmentRequestDto dto = new StudentAppointmentRequestDto();
			dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			dto.setRequestId(String.valueOf(obj[1] != null ? ((Number) obj[1]).longValue() : null));
			dto.setStudentId(obj[2] != null ? obj[2].toString() : null);
			dto.setAuthorityType(obj[3] != null ? obj[3].toString() : null);
			dto.setApprovalLevel(Long.valueOf(obj[4] != null ? ((Number) obj[4]).intValue() : null));
			dto.setValidatingAuthorityEmail(obj[5] != null ? obj[5].toString() : null);
			dto.setValidatingAuthority(obj[6] != null ? obj[6].toString() : null);
			dto.setAuthenticationType(obj[7] != null ? obj[7].toString() : null);
			dto.setCategory(obj[8] != null ? obj[8].toString() : null);
			dto.setStatus(obj[9] != null ? obj[9].toString() : null);
			dto.setCreatedBy(obj[10] != null ? obj[10].toString() : null);
			dto.setCreatedAt(obj[11] != null ? String.valueOf(DateUtility.toLocalDateTime(obj[11])) : null);
			dto.setModifiedAt(obj[13] != null ? DateUtility.toLocalDateTime(obj[13]) : null);
			return dto;
		}).collect(Collectors.toList());
	}

	private boolean isStatusRejected(List<String> split) {
		return studentWorkflowRepository
				.checkRejectedDetails(Long.valueOf(split.getFirst()), WorkflowStatus.REJECTED.getStatus(), ModelConstants.STATUS_ACTIVE,
						Constants.CCW,RoleEnum.DEAN.getValue())
				.isPresent();
	}

	public void setDtoValues(StudentAppointmentRequestDto studentAppointmentRequestDto, List<String> split, StudentWorkflowEntity studentWorkflowEntity, List<StudentWorkflowDto> studentWorkflowDtoList) throws Exception {
		studentAppointmentRequestDto.setApprovalStatus(getApprovalStatus(split.get(2)));
		studentAppointmentRequestDto.setEncryptedString(
				studentAppointmentRequestService.encryptWorkflowIdAndModifiedAt(studentWorkflowEntity.getModifiedAt(), studentWorkflowEntity.getId()));
		studentAppointmentRequestDto.setStudentWorkflowDto(studentWorkflowDtoList);
		studentAppointmentRequestDto.setApprovalNotes(studentWorkflowEntity.getApprovalNotes());
		studentAppointmentRequestDto.setStudentFilesInfoDtoList(studentAppointmentRequestService.getStudentFileInfoList(studentAppointmentRequestDto.getId(), studentAppointmentRequestDto.getStudentId()));
	}

	private String getApprovalStatus(String status) {
		return switch (status) {
			case "v" -> WorkflowStatus.VALIDATING.getStatus().toLowerCase();
			case "a" -> WorkflowStatus.APPROVE.getStatus().toLowerCase();
			case "r" -> WorkflowStatus.REJECTED.getStatus().toLowerCase();
			case "c" -> WorkflowStatus.COMPLETE.getStatus().toLowerCase();
			case "view" -> WorkflowStatus.VIEW.getStatus().toLowerCase();
			default -> ModelConstants.EMPTY_STRING;
		};
	}

	@Transactional
	public String allotStudent(String studentId, long requestId, Long hostelId, String roomNo, String seatName, String status) {
		StudentAppointmentRequestDto studentAppointmentRequestDto = getStudentAllotmentDetails(hostelId, roomNo, requestId,
				WorkflowStatus.REALLOCATE.getStatus().equalsIgnoreCase(status.trim()) ? WorkflowStatus.ALLOTTED.getStatus() : WorkflowStatus.APPROVED.getStatus(),
				ModelConstants.STATUS_ACTIVE);
		HostelMasterDto hostelMasterDto = hostelMasterService.getHostelDetailsById(hostelId);
		HostelRoomInfoDto hostelRoomInfoDto = HostelRoomInfoMapper.INSTANCE.fromHostelRoomInfoEntity(hostelRoomInfoRepository.findRoomByHostelIdAndRoomNoRecentRecord(ModelConstants.STATUS_ACTIVE, hostelId, roomNo));
		AllStudentsDetailsViewDto allStudentsDetailsViewDto = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);

		if (Objects.nonNull(studentAppointmentRequestDto) && checkHostelGenderType(studentAppointmentRequestDto, hostelMasterDto)) {
			return "In this hostel, " + getMaleOrFemale(hostelMasterDto) + " student cannot be allocated.";
		}

		if (Objects.nonNull(studentAppointmentRequestDto)){
			VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto = vacationHostelRoomAllotmentInfoRepository
					.getAllotStudentDetails(
							studentId,
							studentAppointmentRequestDto.getStayFrom(),
							studentAppointmentRequestDto.getStayTo(),
							ModelConstants.STATUS_ACTIVE)
					.map(VacationHostelRoomAllotmentInfoMapper.INSTANCE::toDto)
					.orElse(null);

			if (Objects.nonNull(vacationHostelRoomAllotmentInfoDto)){
				updateVacatingHostelRoomAllotmentInfo(vacationHostelRoomAllotmentInfoDto, hostelRoomInfoDto, requestId);
			} else {
				saveVacationHostelRoomAllotmentInfo(allStudentsDetailsViewDto, studentAppointmentRequestDto, hostelRoomInfoDto, requestId, seatName);
			}
			String appStatus=WorkflowStatus.ALLOTTED.getStatus();
			String statusNotes="You have been allotted to the " + hostelMasterDto.getHostelName() + " - Room Number " + roomNo;
			updateStatusInStudentAppointmentRequest(requestId,appStatus,statusNotes);
			return Constants.SAVED;
		} else {
			return Constants.ERROR;
		}
	}

	private String getMaleOrFemale(HostelMasterDto hostelMasterDto) {
		return hostelMasterDto.getHostelGenderType().equalsIgnoreCase(Constants.MALE) ? Constants.MALE_FULL_FORM : Constants.FEMALE_FULL_FORM;
	}

	public void updateVacatingHostelRoomAllotmentInfo(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto, HostelRoomInfoDto hostelRoomInfoDto, Long requestId) {
		VacationHostelRoomAllotmentInfoEntity entity = vacationHostelRoomAllotmentInfoRepository.findById(vacationHostelRoomAllotmentInfoDto.getId()).orElse(null);
		Objects.requireNonNull(entity).setRequestid(String.valueOf(requestId));
		entity.setStudentName(vacationHostelRoomAllotmentInfoDto.getStudentName());
		entity.setDob(vacationHostelRoomAllotmentInfoDto.getDob());
		entity.setEmail(vacationHostelRoomAllotmentInfoDto.getEmail());
		entity.setStayFromDate(vacationHostelRoomAllotmentInfoDto.getStayFromDate());
		entity.setStayToDate(vacationHostelRoomAllotmentInfoDto.getStayToDate());
		entity.setNatureOfAppointment(vacationHostelRoomAllotmentInfoDto.getNatureOfAppointment());
		entity.setDiningRequired(vacationHostelRoomAllotmentInfoDto.getDiningRequired());
		entity.setStayId(vacationHostelRoomAllotmentInfoDto.getStayId() == null ? String.valueOf(0) : vacationHostelRoomAllotmentInfoDto.getStayId());
		entity.setRoomid(HostelRoomInfoMapper.INSTANCE.toHostelRoomInfoEntity(hostelRoomInfoDto));
		entity.setBuilding(HostelFloorMasterMapper.INSTANCE.toHostelFloorMasterEntity(hostelRoomInfoDto.getBuilding()));
		entity.setStudentId(vacationHostelRoomAllotmentInfoDto.getStudentId() != null ? vacationHostelRoomAllotmentInfoDto.getStudentId() : null);
		entity.setSubRoomid(vacationHostelRoomAllotmentInfoDto.getSubRoomid());
		entity.setStayType(vacationHostelRoomAllotmentInfoDto.getStayType());
		entity.setGender(vacationHostelRoomAllotmentInfoDto.getGender().toUpperCase());
		vacationHostelRoomAllotmentInfoRepository.saveAndFlush(entity);
	}

	private boolean checkHostelGenderType(StudentAppointmentRequestDto dto, HostelMasterDto hostel) {
		return !hostel.getHostelGenderType().equalsIgnoreCase(dto.getGender());
	}

	public void updateStatusInStudentAppointmentRequest(Long requestId, String status, String statusNotes) {
		Optional<StudentAppointmentRequestEntity> studentAppointmentRequest = studentAppointmentRequestRepository.findByIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE);
		if (studentAppointmentRequest.isPresent()){
			StudentAppointmentRequestEntity studentAppointmentRequestEntity = studentAppointmentRequest.get();
			studentAppointmentRequestEntity.setModifiedBy(SecurityCtxUtil.userName());
			studentAppointmentRequestEntity.setModifiedAt(LocalDateTime.now());
			studentAppointmentRequestEntity.setStatus(status);
			studentAppointmentRequestEntity.setStatusNotes(statusNotes);
			studentAppointmentRequestRepository.save(studentAppointmentRequestEntity);
		}
	}

	public void saveVacationHostelRoomAllotmentInfo(AllStudentsDetailsViewDto allStudentsDetailsViewDto, StudentAppointmentRequestDto studentAppointmentRequestDto, HostelRoomInfoDto hostelRoomInfoDto, Long requestId, String seatName) {
		VacationHostelRoomAllotmentInfoEntity entity = new VacationHostelRoomAllotmentInfoEntity();
		entity.setRequestid(String.valueOf(requestId));
		entity.setStudentName(allStudentsDetailsViewDto.getStudentName());
		entity.setDob(allStudentsDetailsViewDto.getDob());
		entity.setEmail(allStudentsDetailsViewDto.getStudentPersonalEmail());
		entity.setStayFromDate(studentAppointmentRequestDto.getStayFrom());
		entity.setStayToDate(studentAppointmentRequestDto.getStayTo());
		entity.setNatureOfAppointment(studentAppointmentRequestDto.getCategory());
		entity.setDiningRequired(studentAppointmentRequestDto.getDining());
		entity.setStayId(String.valueOf(0L));
		entity.setRoomid(HostelRoomInfoMapper.INSTANCE.toHostelRoomInfoEntity(hostelRoomInfoDto));
		entity.setBuilding(HostelFloorMasterMapper.INSTANCE.toHostelFloorMasterEntity(hostelRoomInfoDto.getBuilding()));
		entity.setStudentId(studentAppointmentRequestDto.getStudentId());
		entity.setSubRoomid(seatName);
		entity.setGender(allStudentsDetailsViewDto.getGender().toUpperCase());
		vacationHostelRoomAllotmentInfoRepository.save(entity);
	}

	public StudentAppointmentRequestDto getStudentAllotmentDetails(Long hostelId, String roomNo, Long requestId, String status, String activeFlag) {
        return hostelRoomInfoRepository.getAllotStudent(hostelId, roomNo, requestId, status, activeFlag)
				.flatMap(resultList -> resultList.stream().findFirst())
				.map(row -> {
					StudentAppointmentRequestDto dto = new StudentAppointmentRequestDto();
					if (row.length > 0 && row[0] != null) dto.setStayFrom(LocalDate.parse(row[0].toString()));
					if (row.length > 1 && row[1] != null) dto.setStayTo(LocalDate.parse(row[1].toString()));
					if (row.length > 2 && row[2] != null) dto.setCategory(row[2].toString());
					if (row.length > 3 && row[3] != null) dto.setDining(row[3].toString());
					if (row.length > 4 && row[4] != null) dto.setStudentId(row[4].toString());
					if (row.length > 5 && row[5] != null) dto.setStudentName(row[5].toString());
					if (row.length > 6 && row[6] != null) dto.setDob(LocalDate.parse(row[6].toString()));
					if (row.length > 7 && row[7] != null) dto.setGender(row[7].toString());
					if (row.length > 8 && row[8] != null) dto.setStudentIITMSmail(row[8].toString());
					if (row.length > 9 && row[9] != null) dto.setRoomId(row[9].toString());
					if (row.length > 10 && row[10] != null) dto.setBuildingId(row[10].toString());

					return dto;
				})
				.orElse(null);
	}
	
	public FilterCriteriaDto getFilterData(PaginationForm form, String url) {
		FilterCriteriaDto filterCriteria = new FilterCriteriaDto();
		filterCriteria.setValidationStatus(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("validationStatus")));
		filterCriteria.setCategory(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("category")));
		filterCriteria.setApprovalFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("approvalFromDate")));
		filterCriteria.setApprovalToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("approvalToDate")));
		filterCriteria.setSubmittedFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedFromDate")));
		filterCriteria.setSubmittedToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedToDate")));
		filterCriteria.setAppointmentFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("appointmentFromDate")));
		filterCriteria.setAppointmentToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("appointmentToDate")));
		filterCriteria.setStayFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("stayFromDate")));
		filterCriteria.setStayToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("stayToDate")));
		filterCriteria.setStudentName(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentName")));
		filterCriteria.setStudentId(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId")));
		filterCriteria.setHostelId(ValidationCommon.toIntegerOrZero(form.getAdditionalParam().get("hostelName")));

        DashboardTabMasterEntity tabEntity = dashboardTabMasterRepository.findByTabTypeAndTabUrl(
                messageSource.getMessage("message.label.tab", null, Locale.getDefault()), url);
        filterCriteria.setTabNo(tabEntity.getProperty());
        filterCriteria.setCurrentDayStayFlag(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("currentDayStayFlag")));
		return filterCriteria;
	}
	
	
	@Transactional
	public String updateCheckInCheckOut(long requestId, String status) {
		String statusNotes="You have been "+ status +" to the alloted hostel";
		updateStatusInStudentAppointmentRequest(requestId, status, statusNotes);
		return status;
	}

    public String bulkApproveReject(BulkApprovalRejectDto dto, HttpServletRequest request) {
		AtomicReference<String> saveStatus = new AtomicReference<>(ModelConstants.EMPTY_STRING);
		StudentAppointmentRequestDto studentAppointmentRequestDto = new StudentAppointmentRequestDto();
		studentAppointmentRequestDto.setStayFrom(dto.getStayFromDate());
		studentAppointmentRequestDto.setStayTo(dto.getStayToDate());
		studentAppointmentRequestDto.setApprovalNotes(dto.getApprovalNote());
		studentAppointmentRequestDto.setRejectionReason(dto.getRejectionReason());
		studentAppointmentRequestDto.setStatus(dto.getApprovalStatus());
		try {
			dto.getEncryptedIds().forEach(s-> {
				try {
					processBulkApproval(s, saveStatus, dto, request, studentAppointmentRequestDto);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			return Constants.SAVED;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR;
		}
    }

    private void processBulkApproval(String s, AtomicReference<String> saveStatus, BulkApprovalRejectDto dto, HttpServletRequest request, StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
		List<String> split;
		try {
			split = List.of(MCrypt.getInstance().decryptToString(s).split(Constants.BACKTICK));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		saveStatus.set(processApprovalBySplit(split, request, dto, studentAppointmentRequestDto));
    }

    private String processApprovalBySplit(List<String> split, HttpServletRequest request, BulkApprovalRejectDto bulkApprovalRejectDto, StudentAppointmentRequestDto studentAppointmentRequestDto) throws Exception {
		StudentWorkflowEntity studentWorkflowEntity = studentWorkflowService.getStudentWorkflowDetails(Long.valueOf(split.get(1)), split.getFirst());
		List<String> newSplitList = new ArrayList<>();

		studentAppointmentRequestDto.setId(studentWorkflowEntity.getRequestId());
		studentAppointmentRequestDto.setStudentId(studentWorkflowEntity.getStudentId());
		newSplitList.add(String.valueOf(studentWorkflowEntity.getId()));
		newSplitList.add(String.valueOf(studentWorkflowEntity.getModifiedAt()));
		newSplitList.add(bulkApprovalRejectDto.getApprovalStatus());
		newSplitList.add(Objects.nonNull(bulkApprovalRejectDto.getRejectionReason()) ? bulkApprovalRejectDto.getRejectionReason() : null);
        return updateApprovalStatus(newSplitList, studentAppointmentRequestDto, request);
    }
}
