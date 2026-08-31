package com.iitm.hosteldine.service.dean;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.dean.DeanAccommodationRequestDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.dean.RegularStudentCheckInDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.dashboard.student.StudentHostelRoomVacatingRequestRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegularStudentCheckInService {
	
	private final SimsConfigDataService simsConfigDataService;
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    private final HostelMasterService hostelMasterService;
	private final StudentHostelRoomVacatingRequestRepository studentHostelRoomVacatingRequestRepository;

	public List<DeanAccommodationRequestDto> getRegularStudentCheckInList(PaginationForm form,
			FilterCriteriaDto filterCriteria, String url, DeanApprovalDto columnDto) throws Exception {
		
		Object[] entityList = fetchRegularStudentCheckInData(form, filterCriteria);

		return Arrays.stream(entityList).map(data -> {
			Object[] objects = (Object[]) data;
			DeanAccommodationRequestDto dto = new DeanAccommodationRequestDto();
			List<PropertyDto> actionList = new ArrayList<PropertyDto>();
			try {
				Long allotmentId = Long.parseLong(objects[0].toString());
				String studentId = getStringValue(objects[5]);
				dto.setHostelName(getStringValue(objects[1]));
				dto.setRoomNo(getStringValue(objects[2]));
				dto.setSeat(getStringValue(objects[3]));
				dto.setFirstName(getStringValue(objects[4]));
				dto.setApplicationId(studentId);
				dto.setCheckInStatus(objects[8] != null && objects[8].equals(WorkflowStatus.CHECKED_IN.getStatus()) ? "Checked-In"
								: Strings.EMPTY);
				dto.setCheckInDate(objects[10] != null
								? new SimpleDateFormat(Constants.FRONTEND_DATE_MON_YEAR_TIME_FORMAT)
								.format(new SimpleDateFormat(Constants.BACKEND_DATE_MON_YEAR_TIME_FORMAT)
								.parse(String.valueOf(objects[10]))) : null);
				dto.setStayFrom(getStringValue(objects[14]));
				dto.setStayTo(getStringValue(objects[7]));

				// shifted_date
				LocalDate shiftedStatus = (objects[7] != null)
					    ? ((java.sql.Date) objects[7]).toLocalDate()
					    : null;
				// approval_date
				LocalDate vacationStatus = (objects[9] != null)
					    ? ((java.sql.Date) objects[9]).toLocalDate()
					    : null;
				// allocation_status
				String allocationStatus = (objects[8] != null) ? String.valueOf(objects[8]) : null;
				String vacationStudentId = (objects[13] != null) ? String.valueOf(objects[13]) : null;
				Boolean vacationCheckoutStatus = objects[11] != null ? Boolean.valueOf(String.valueOf(objects[11]))
						: false;

				List<String> allowedRoles = simsConfigDataService
						.getSimConfigValueFromJsonArray(SimsConfigDataService.REGULAR_CHECKIN_ALLOWED_ROLES)
						.stream().map(SimsConfigDataJsonArrayDto::getValue).toList();
				for (PropertyDto action : columnDto.getActionUrlList()) {
					if (allowedRoles.contains(SecurityCtxUtil.userRole())) {
						String statusURL = "/status?data=";
						if (allocationStatus == null && shiftedStatus == null) {
							if (action.getPropertyValue().equals(WorkflowStatus.CHECKED_IN.getStatus())) {
								PropertyDto actionDto = new PropertyDto();
								actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
								actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
								actionDto.setDisplayName(WorkflowStatus.CHECK_IN.getStatus());
								actionDto.setUrl(url + statusURL + encryptRegularStudentCheckinUrl(studentId,
										allotmentId, WorkflowStatus.CHECKED_IN.getStatus()));
								actionList.add(actionDto);
							}
						}
						if (allocationStatus != null || shiftedStatus != null) {
							if (action.getPropertyValue().equals(WorkflowStatus.CHECKED_OUT.getStatus())) {
								if (vacationStatus == null && shiftedStatus != null) {
									PropertyDto actionDto = new PropertyDto();
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(WorkflowStatus.CHANGE_CHECK_OUT.getStatus());
									actionDto.setUrl(url + statusURL + encryptRegularStudentCheckinUrl(studentId,
											allotmentId, WorkflowStatus.CHECKED_OUT.getStatus()));
									actionList.add(actionDto);
								} else if (vacationStatus != null) {
									PropertyDto actionDto = new PropertyDto();
									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
									actionDto.setDisplayName(WorkflowStatus.VACATE_CHECK_OUT.getStatus());
									actionDto.setUrl(url + statusURL + encryptRegularStudentCheckinUrl(studentId,
											allotmentId, WorkflowStatus.CHECKED_OUT.getStatus()));
									actionList.add(actionDto);
								}
//								else if (vacationStatus == null && shiftedStatus == null && vacationCheckoutStatus
//										&& vacationStudentId == null) {
//									PropertyDto actionDto = new PropertyDto();
//									actionDto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
//									actionDto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
//									actionDto.setDisplayName(WorkflowStatus.VACATION_CHECK_OUT.getStatus());
//									actionDto.setUrl(url + statusURL + encryptRegularStudentCheckinUrl(studentId,
//											allotmentId, WorkflowStatus.VACATION_CHECKED_OUT.getStatus()));
//									actionList.add(actionDto);
//								}
							}
						}
					}
				}
				dto.setActionList(actionList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dto;
		}).toList();
	}

	public RegularStudentCheckInDto getCardDetails(FilterCriteriaDto filterCriteria) {
		List<HostelMasterDto> userHostelDetails = null;
		ArrayList<String> listOfUser = simsConfigDataService
				.getSimConfigValueArrayList(SimsConfigDataService.REGULAR_CHECKIN_USER_LIST);
		if (listOfUser.contains(SecurityCtxUtil.userName())) {
			userHostelDetails = hostelMasterService.getHostelList();
		} else {
			userHostelDetails = hostelMasterService.getHostelListByUser();
		}

		if (userHostelDetails != null && !userHostelDetails.isEmpty() && filterCriteria.getHostelId() != null) {
		  Optional<HostelMasterDto> matchedHostel = userHostelDetails.stream()
			        .filter(hostel -> hostel.getId() == filterCriteria.getHostelId().longValue())
			        .findFirst();
		    if (matchedHostel.isPresent()) {
				RegularStudentCheckInDto dto = new RegularStudentCheckInDto();

				List<Object[]> occupancyCount = hostelRoomAllotmentRepository
						.getOccupancyCount(filterCriteria.getHostelId());
				if (!occupancyCount.isEmpty()) {
					Object[] occupancy = occupancyCount.get(0);
					dto.setTotalRooms(ValidationCommon.toLongOrZero(occupancy[5]));
					dto.setRoomsAllotted(ValidationCommon.toLongOrZero(occupancy[6]));
					dto.setRoomsFree(ValidationCommon.toLongOrZero(occupancy[7]));
					dto.setTotalNoOfSeats(ValidationCommon.toLongOrZero(occupancy[2]));
					dto.setAllotted(ValidationCommon.toLongOrZero(occupancy[3]));
					dto.setExactVacancy(ValidationCommon.toLongOrZero(occupancy[4]));
				} else {
					dto.setTotalRooms(0L);
					dto.setRoomsAllotted(0L);
					dto.setRoomsFree(0L);
					dto.setTotalNoOfSeats(0L);
					dto.setAllotted(0L);
					dto.setExactVacancy(0L);
				}

				List<Object[]> checkInAndOutCount = hostelRoomAllotmentRepository
						.getCheckInAndOutCount(filterCriteria.getHostelId());
				if (!checkInAndOutCount.isEmpty()) {
					Object[] checkInAndOut = checkInAndOutCount.get(0);
					dto.setOccupied(ValidationCommon.toLongOrZero(checkInAndOut[0]));
					dto.setCheckInCount(ValidationCommon.toLongOrZero(checkInAndOut[1]));
					dto.setCheckOutCount(ValidationCommon.toLongOrZero(checkInAndOut[2]));
					dto.setVacatedCount(ValidationCommon.toLongOrZero(checkInAndOut[3]));
				} else {
					dto.setOccupied(0L);
					dto.setCheckInCount(0L);
					dto.setCheckOutCount(0L);
					dto.setVacatedCount(0L);
				}
				return dto;
			}
		}
		return null;
	}

	public static String encryptRegularStudentCheckinUrl(String studentId, Long allotmentId, String status)
			throws Exception {
		String encryptKey = studentId + Constants.BACKTICK + allotmentId + Constants.BACKTICK + status
				+ Constants.BACKTICK + Utility.getCurrentTimeStamp();
		return MCrypt.getInstance().encryptToText(encryptKey);
	}

	public List<String> getFilterList(String userName) {
		ArrayList<String> userList = simsConfigDataService
				.getSimConfigValueArrayList(SimsConfigDataService.REGULAR_CHECKIN_USER_LIST);
		if (userList.contains(userName)) {
			return List.of("fromDate", "toDate", "studentId", "hostelNameMandatory");
		} else {
			return List.of("fromDate", "toDate", "hostelNameMandatory");
		}
	}

	public FilterCriteriaDto getFilterData(PaginationForm form) {
		FilterCriteriaDto filterCriteria = new FilterCriteriaDto();
		filterCriteria.setFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("fromDate")));
		filterCriteria.setToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("toDate")));
		filterCriteria.setHostelId(ValidationCommon.toIntegerOrZero(form.getAdditionalParam().get("hostelNameMandatory")));
		filterCriteria.setStudentId(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId")));
		return filterCriteria;
	}

	public Object[] fetchRegularStudentCheckInData(PaginationForm form, FilterCriteriaDto filterCriteria) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		ArrayList<String> userList = simsConfigDataService
				.getSimConfigValueArrayList(SimsConfigDataService.REGULAR_CHECKIN_USER_LIST);
		if (filterCriteria.getFromDate() == null && filterCriteria.getToDate() == null) {
			if (userList.contains(SecurityCtxUtil.userName())) {
				return hostelRoomAllotmentRepository.getHotelAllotmentList(filterCriteria.getHostelId(),
						filterCriteria.getStudentId());
			} else {
				return hostelRoomAllotmentRepository.getHotelAllotmentList(filterCriteria.getHostelId());
			}
		} else if (filterCriteria.getFromDate() != null && filterCriteria.getToDate() == null) {
			return hostelRoomAllotmentRepository.getHotelAllotmentListByFromDate(filterCriteria.getHostelId(),
					filterCriteria.getFromDate());
		} else if (filterCriteria.getFromDate() == null && filterCriteria.getToDate() != null) { 
			return hostelRoomAllotmentRepository.getHotelAllotmentListByToDate(filterCriteria.getHostelId(),
					filterCriteria.getToDate());
		} else {
			return hostelRoomAllotmentRepository.getHotelAllotmentListFilterDates(filterCriteria.getHostelId(),
					filterCriteria.getFromDate(), filterCriteria.getToDate());
		}
	}

	@Transactional
	public String saveChecKInAndCheckOutStatus(String data) throws Exception {
		String[] split = MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK);
		String studentId = ValidationCommon.toString(split[0]);
		Long allotmentId = Long.parseLong(split[1]);
		String status = ValidationCommon.toString(split[2]);

		//Get vacating form details
		List<Object[]> vacateDateObj = studentHostelRoomVacatingRequestRepository.getHostelRoomVacatingView(
				studentId, WorkflowStatus.APPROVED.getStatus(), ModelConstants.STATUS_ACTIVE);
		LocalDate vacateDate = null;
		long vacatingId=0;
		if (!vacateDateObj.isEmpty() && vacateDateObj.get(0)[0] != null) {
			vacatingId=Long.valueOf(vacateDateObj.get(0)[0].toString());
			vacateDate = LocalDate.parse(vacateDateObj.get(0)[1].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (status.equals(WorkflowStatus.CHECKED_IN.getStatus())) {
			int updateCount = hostelRoomAllotmentRepository.updateStudentCheckInStatus(status, LocalDateTime.now(),
					studentId, allotmentId, ModelConstants.STATUS_ACTIVE, SecurityCtxUtil.userName(),
					DateUtility.getNowTimeInstant());
			if (updateCount > 0) {
				if (vacatingId != 0 && vacateDate != null) {
					HostelRoomAllotmentInfoEntity hostelRoomAllotmentInfoEntity =
							hostelRoomAllotmentRepository.findByRoomAllotmentIdAndStudentIdAndActiveFlag
									(allotmentId,studentId,ModelConstants.STATUS_ACTIVE).orElse(null);
					if (Objects.nonNull(hostelRoomAllotmentInfoEntity)) {
						if (vacateDate.isBefore(hostelRoomAllotmentInfoEntity.getStayFromDate())) {
							int cnt = studentHostelRoomVacatingRequestRepository.
									updateRejoiningDate(SecurityCtxUtil.userId(), DateUtility.getNowTimeInstant(),
											LocalDate.now(),vacatingId);
						}
					}
				}
				return "message.label.student.checkin.successfully";
			}
		} else if (status.equals(WorkflowStatus.VACATION_CHECKED_OUT.getStatus())) {
			int updateCount = hostelRoomAllotmentRepository.updateStudentVacationCheckOutStatus(status, LocalDate.now(),
					studentId, allotmentId, ModelConstants.STATUS_ACTIVE, SecurityCtxUtil.userName(),
					DateUtility.getNowTimeInstant());
			if (updateCount > 0) {
				return "message.label.student.checkout.successfully";
			}
		} else {

			if (vacatingId != 0) {
				int updateCount = hostelRoomAllotmentRepository.updateStudentVacateCheckOutStatus(status, vacateDate,
						LocalDateTime.now(), studentId, allotmentId, ModelConstants.STATUS_ACTIVE,
						WorkflowStatus.CHECKED_IN.getStatus(), SecurityCtxUtil.userName(),
						DateUtility.getNowTimeInstant());
				if (updateCount > 0) {
					return "message.label.student.checkout.successfully";
				}
			} else {
				int updateCount = hostelRoomAllotmentRepository.updateStudentVacateCheckOutStatus(status,
						LocalDateTime.now(), studentId, allotmentId, ModelConstants.STATUS_ACTIVE,
						WorkflowStatus.CHECKED_IN.getStatus(), SecurityCtxUtil.userName(),
						DateUtility.getNowTimeInstant());
				if (updateCount > 0) {
					return "message.label.student.checkout.successfully";
				}
			}
		}
		return null;
	}

	private String getStringValue(Object obj) {
		return obj != null && !String.valueOf(obj).trim().isEmpty() ? String.valueOf(obj) : Constants.HYPHEN;
	}
}