package com.iitm.hosteldine.service.mess;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.mapper.OtherCandidate.CandidateProfileMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInfoMapper;
import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateProfileRepository;
import com.iitm.hosteldine.repository.hostel.VacationHostelRoomAllotmentInfoRepository;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.mess.MessOption;
import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.mess.AccomodationDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.mess.StayExtensionDto;
import com.iitm.hosteldine.dto.mess.TemporaryAccomodationDto;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerAEntity;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerAEntityId;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerBEntity;
import com.iitm.hosteldine.entity.mess.AccomodationLedgerBEntityId;
import com.iitm.hosteldine.exception.RecordListEmptyException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.OtherCandidate.CandidateWorkflowEntity;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentAdviceEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateAppointmentRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateWorkflowRepository;
import com.iitm.hosteldine.repository.OtherCandidate.TempAccomPaymentAdviceRepository;
import com.iitm.hosteldine.repository.hostel.HostelFloorMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.mess.AccomodationLedgerARepository;
import com.iitm.hosteldine.repository.mess.AccomodationLedgerBRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.mess.TemporaryAccomodationRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.financialYear.FinancialYearService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemporaryAccomodationService {

	private final TemporaryAccomodationRepository temporaryAccomodationRepository;
	private final CandidateWorkflowRepository candidateWorkflowRepository;
	private final TempAccomPaymentAdviceRepository tempAccomPaymentAdviceRepository;
	private final CandidateAppointmentRequestRepository candidateAppointmentRequestRepository;
	private final MessMasterRepository messMasterRepository;
	private final FinancialYearService financialYearService;
	private final AccomodationLedgerARepository accomodationLedgerARepository;
	private final AccomodationLedgerBRepository accomodationLedgerBRepository;
	private final MessMasterService messMasterService;
	private final MessageSource messageSource;
	private final Utility utility;
	private final HostelMasterService hostelMasterService;
	private final HostelFloorMasterRepository hostelFloorMasterRepository;
	private final HostelRoomInfoRepository hostelRoomInfoRepository;
	private final SimsConfigDataService simsConfigDataService;
    private final VacationHostelRoomAllotmentInfoRepository vacationHostelRoomAllotmentInfoRepository;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final CandidateProfileRepository candidateProfileRepository;
	private final CommonResponseUtil commonResponseUtil;

	public Page<TemporaryAccomodationDto> getTemporaryAccomodationList(PaginationForm form, String url) throws Exception {
		Page<Object[]> result = fetchTemporaryAccomodationList(form);
		return setTemporaryAccomodationListValues(result, url);
	}

	private Page<Object[]> fetchTemporaryAccomodationList(PaginationForm form) {
		String candidateName = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("candidateName"));
		String requestId = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("requestId"));
		String approvalFromStr = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("approvalFromDate"));
//		String approvalToStr = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("approvalToDate"));
		LocalDateTime approvalFrom = null;
//		LocalDateTime approvalTo = null;
		if (approvalFromStr != null)
			approvalFrom = LocalDate.parse(approvalFromStr).atStartOfDay();
//		if (approvalToStr != null)
//			approvalTo = LocalDate.parse(approvalToStr).atTime(23, 59, 59);
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> result = Page.empty();
		result = temporaryAccomodationRepository.getTemporaryAccomodationList(null, null, null, null, candidateName, requestId, null, approvalFrom, pageable);
		return result;
	}

	private Page<TemporaryAccomodationDto> setTemporaryAccomodationListValues(Page<Object[]> result, String url) {
		return result.map(objects -> {
			TemporaryAccomodationDto dto = new TemporaryAccomodationDto();
			dto.setRequestId(utility.parseLong(objects[0]));
			dto.setCreatedAt(ValidationCommon.formatDateString(objects[1], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setStayFrom(ValidationCommon.formatDateString(objects[3], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setStayTo(ValidationCommon.formatDateString(objects[4], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setCandidateName(ValidationCommon.toString(objects[5]));
			dto.setGender(Utility.getGender(objects[6]));
			dto.setEmail(ValidationCommon.toString(objects[7]));
			dto.setCandidateId(utility.parseLong(objects[2]));
			dto.setUrl(getUrl(url, dto.getCandidateId(), utility.parseLong(objects[0]), null, messageSource.getMessage("url.view", null, Locale.getDefault())));
			dto.setNetAmount(utility.parseDouble(objects[8]));
			dto.setApprovalDate(ValidationCommon.formatDateString(objects[9], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));

			return dto;
		});
	}

	private String getUrl(String url, Long candidateId, Long requestId, Long paymentAdviceId, String action) {
		try {
			url = url + action + "?data=" + encryptAccommodationRequestUrl(candidateId, requestId, paymentAdviceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public TemporaryAccomodationDto getPaymentAdviceDetails(Long candidateId, Long requestId, String url) throws RecordListEmptyException {
		TemporaryAccomodationDto temporaryAccomodationDto = new TemporaryAccomodationDto();
		temporaryAccomodationDto.setRequestId(requestId);
		temporaryAccomodationDto.setCandidateId(candidateId);
		fetchCandidateDetails(candidateId, temporaryAccomodationDto);
		fetchAppointmentDetails(candidateId, requestId, temporaryAccomodationDto);
		fetchCandidateWorkflowDetails(candidateId, requestId, temporaryAccomodationDto);
		fetchStayExtensionList(requestId, temporaryAccomodationDto);
		fetchAccomodationList(temporaryAccomodationDto);
		temporaryAccomodationDto.setMessMap(fetchMessListAsMap());
		fetchPaymentAdviceList(requestId, temporaryAccomodationDto, candidateId, url);
		return temporaryAccomodationDto;
	}

	private void fetchCandidateDetails(Long candidateId, TemporaryAccomodationDto dto) throws RecordListEmptyException {
		Object[] candidateDetails = temporaryAccomodationRepository.getCandidateDetails(candidateId);
		if (candidateDetails == null || candidateDetails.length == 0) {
			throw new RecordListEmptyException(messageSource.getMessage("message.candidate.details.empty", null, Locale.getDefault()));
		}
		Object[] data = (Object[]) candidateDetails[0];
		dto.setCandidateId(utility.parseLong(data[0]));
		dto.setFirstName(ValidationCommon.toString(data[1]));
		dto.setLastName(ValidationCommon.toString(data[2]));
		dto.setEmail(ValidationCommon.toString(data[12]));
		dto.setDob(ValidationCommon.formatDate(data[3], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setGender(Utility.getGender(data[4]));
		dto.setAddress1(ValidationCommon.toString(data[5]));
		dto.setAddress2(ValidationCommon.toString(data[6]));
		dto.setCity(ValidationCommon.toString(data[7]));
		dto.setState(ValidationCommon.toString(data[9]));
		dto.setPincode(utility.parseInt(data[8]));
		dto.setPhoneNumber(ValidationCommon.toString(data[10]));
		dto.setMobileNumber(ValidationCommon.toString(data[11]));
		dto.setCandidateCategory(ValidationCommon.toString(data[18]));
		if (dto.getCandidateId() != null) {
			dto.setImageId(dto.getCandidateId() + ModelConstants.UNDERSCORE + ModelConstants.FILE_STUDENT_PROFILE + ModelConstants.DEFAULT_FILE_EXTENSION);
		}
	}

	private void fetchAppointmentDetails(Long candidateId, Long requestId, TemporaryAccomodationDto dto) throws RecordListEmptyException {
		String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
		if (RoleEnum.CCW_DEAN.getValue().equalsIgnoreCase(userRole)) {
			userRole = RoleEnum.DEAN.getValue();
		}
		Object[] appointmentDetails = temporaryAccomodationRepository.getAppointmentDetails(candidateId, requestId, userRole);
		if (appointmentDetails == null || appointmentDetails.length == 0) {
			throw new RecordListEmptyException(messageSource.getMessage("message.appointment.details.empty", null, Locale.getDefault()));
		}
		Object[] data = (Object[]) appointmentDetails[0];
		dto.setAppointmentFrom(ValidationCommon.formatDate(data[3], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setAppointmentTo(ValidationCommon.formatDate(data[4], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setStayRequestFrom(ValidationCommon.formatDate(data[5], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setStayRequestTo(ValidationCommon.formatDate(data[6], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
		dto.setGrossPay(utility.parseDouble(data[7]));
		dto.setNatureOfAppointment(ValidationCommon.toString(data[8]));
		dto.setValidatingAuthorityName(ValidationCommon.toString(data[12]));
		dto.setValidatingAuthorityEmail(ValidationCommon.toString(data[13]));
		String messOptionValue = ValidationCommon.toString(data[33]);
		MessOption messOption = MessOption.fromDbValue(messOptionValue);
		dto.setMessOption(messOption != null ? messOption.getValue() : ModelConstants.NOT_APPLICABLE);
		dto.setPurpose(ValidationCommon.toString(data[31]));
	}

	private void fetchCandidateWorkflowDetails(Long candidateId, Long requestId, TemporaryAccomodationDto temporaryAccomodationDto) {
		List<CandidateWorkflowEntity> candidateWorkflowEntityList = candidateWorkflowRepository.findAllByCandidateIdAndApplicationIdAndActiveFlagOrderByApprovalLevelAsc(candidateId, requestId,
				Constants.ACTIVE_FLAG);
		StringBuilder approvalStatusDetails = new StringBuilder();
		for (CandidateWorkflowEntity workflowEntity : candidateWorkflowEntityList) {
			approvalStatusDetails.append(workflowEntity.getValidatorName() + "   (" + workflowEntity.getEmail() + ") - ");
			String status = workflowEntity.getStatus();
			if (WorkflowStatus.APPROVED.getStatus().equals(status) || WorkflowStatus.REJECTED.getStatus().equals(status)) {
				String modifiedDate = workflowEntity.getModifiedAt() != null
						? (utility.convertToLocalDate(workflowEntity.getModifiedAt()).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)))
						: "";
				approvalStatusDetails.append(status + " " + Constants.ON + " " + modifiedDate);
			} else if (WorkflowStatus.DEFAULT.getStatus().equals(status) || WorkflowStatus.PENDING.getStatus().equals(status)) {
				approvalStatusDetails.append(WorkflowStatus.PENDING.getStatus());
			}
			approvalStatusDetails.append("<br>");
		}
		temporaryAccomodationDto.setApprovalStatusDetails(approvalStatusDetails.toString());
	}

	private void fetchStayExtensionList(Long requestId, TemporaryAccomodationDto dto) {
		Object[] stayExtensionDetails = temporaryAccomodationRepository.getStayExtensionList(requestId, WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.VALIDATING.getStatus(), getStayStatusList(),
				getAppStatusList());
		List<StayExtensionDto> stayExtensionList = new ArrayList<>();

		if (stayExtensionDetails != null) {
			for (Object detail : stayExtensionDetails) {
				Object[] data = (Object[]) detail;
				StayExtensionDto stayExtensionDto = new StayExtensionDto();
				stayExtensionDto.setStayFrom(ValidationCommon.formatDate(data[7], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
				stayExtensionDto.setStayTo(ValidationCommon.formatDate(data[8], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
				stayExtensionDto.setApprovalStatus(ValidationCommon.toString(data[9]));
				stayExtensionList.add(stayExtensionDto);
			}
		}
		dto.setStayExtensionList(stayExtensionList);
	}

	private void fetchAccomodationList(TemporaryAccomodationDto dto) {
		Object[] accomodationDetails = temporaryAccomodationRepository.getAccomodationList();
		List<AccomodationDto> accomodationList = new ArrayList<>();

		if (accomodationDetails != null) {
			for (Object detail : accomodationDetails) {
				Object[] data = (Object[]) detail;
				AccomodationDto accomodationDto = new AccomodationDto();
				accomodationDto.setId(utility.parseLong(data[0]));
				accomodationDto.setCategoryName(ValidationCommon.toString(data[1]));
				accomodationDto.setPaymentPerDay(utility.parseInt(data[2]));
				accomodationDto.setBreakfastCoupon(utility.parseInt(data[3]));
				accomodationDto.setLunchCoupon(utility.parseInt(data[4]));
				accomodationDto.setDinnerCoupon(utility.parseInt(data[5]));
				accomodationList.add(accomodationDto);
			}
		}
		dto.setAccomodationList(accomodationList);
	}

	public Map<Long, String> fetchMessListAsMap() {
		List<MessMasterDto> messList = messMasterService.getMessMasterList();
		Map<Long, String> messMap = new HashMap<>();
		for (MessMasterDto mess : messList) {
			messMap.put(mess.getId(), mess.getMessName());
		}
		return messMap;
	}

	private void fetchPaymentAdviceList(Long requestId, TemporaryAccomodationDto dto, Long candidateId, String url) {
		Object[] paymentAdviceList = temporaryAccomodationRepository.getPaymentAdviceList(requestId);
		List<TempAccomPaymentAdviceDto> paymentList = new ArrayList<>();
		int count = 1;
		if (paymentAdviceList != null) {
			for (Object detail : paymentAdviceList) {
				Object[] data = (Object[]) detail;
				TempAccomPaymentAdviceDto tempAccomPaymentAdviceDto = new TempAccomPaymentAdviceDto();
				tempAccomPaymentAdviceDto.setSlNo(count++);
				tempAccomPaymentAdviceDto.setId(utility.parseLong(data[0]));
				tempAccomPaymentAdviceDto.setRequestId(utility.parseLong(data[1]));
				tempAccomPaymentAdviceDto.setHostelPayFromDate(data[2] != null ? ((java.sql.Date) data[2]).toLocalDate() : null);
				tempAccomPaymentAdviceDto.setHostelPayToDate(data[3] != null ? ((java.sql.Date) data[3]).toLocalDate() : null);
				;
				tempAccomPaymentAdviceDto.setCategoryName(ValidationCommon.toString(data[4]));
				tempAccomPaymentAdviceDto.setMessPayFromDate(data[5] != null ? ((java.sql.Date) data[5]).toLocalDate() : null);
				tempAccomPaymentAdviceDto.setMessPayToDate(data[6] != null ? ((java.sql.Date) data[6]).toLocalDate() : null);
				tempAccomPaymentAdviceDto.setOverallAmount(utility.parseLong(data[7]));
				tempAccomPaymentAdviceDto.setPaymentStatus(ValidationCommon.toString(data[8]));
				tempAccomPaymentAdviceDto.setCardStatus(ValidationCommon.toString(data[10]));
				tempAccomPaymentAdviceDto.setPaymentDate(ValidationCommon.formatDate(data[11], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
				tempAccomPaymentAdviceDto.setPaymentApprovalStatus(ValidationCommon.toString(data[12]));
				tempAccomPaymentAdviceDto.setPdfUrl(getUrl(url, candidateId, requestId, utility.parseLong(data[0]), messageSource.getMessage("url.open.pdf", null, Locale.getDefault())));
				tempAccomPaymentAdviceDto
						.setHostelViewUrl(getUrl(url, candidateId, requestId, utility.parseLong(data[0]), messageSource.getMessage("url.payment.advice.hostel.view", null, Locale.getDefault())));
				paymentList.add(tempAccomPaymentAdviceDto);
			}
		}
		dto.setPaymentAdviceList(paymentList);
	}

	@Transactional
	public boolean savePaymentAdvice(TempAccomPaymentAdviceDto dto) throws Exception {
		TempAccomPaymentAdviceEntity entity = new TempAccomPaymentAdviceEntity();
		try {
			entity.setCategoryId(dto.getCategoryId());
			entity.setCandidateRequest(candidateAppointmentRequestRepository.getReferenceById(dto.getRequestId()));
			entity.setHostelPayFromDate(dto.getHostelPayFromDate());
			entity.setHostelPayToDate(dto.getHostelPayToDate());
			entity.setHostelTotalNoOfDays(dto.getHostelTotalNoOfDays());
			entity.setHostelRatePerDay(dto.getHostelRatePerDay());
			entity.setMessPayFromDate(dto.getMessPayFromDate());
			entity.setMessPayToDate(dto.getMessPayToDate());
			entity.setNoOfBreakfastCoupons(dto.getNoOfBreakfastCoupons());
			entity.setNoOfLunchCoupons(dto.getNoOfLunchCoupons());
			entity.setNoOfDinnerCoupons(dto.getNoOfDinnerCoupons());
			entity.setBreakfastCouponRate(dto.getBreakfastCouponRate());
			entity.setLunchCouponRate(dto.getLunchCouponRate());
			entity.setDinnerCouponRate(dto.getDinnerCouponRate());
			entity.setTotalBreakfastAmount(dto.getTotalBreakfastAmount());
			entity.setTotalLunchAmount(dto.getTotalLunchAmount());
			entity.setTotalDinnerAmount(dto.getTotalDinnerAmount());
			if (dto.getMessId() != null) {
				entity.setMessMaster(messMasterRepository.getReferenceById(dto.getMessId()));
			}
			entity.setCardCharges(dto.getCardCharges());
			entity.setMessaccAmount(dto.getMessaccAmount());
			entity.setOverallAmount(dto.getOverallAmount());
			entity.setPaymentStatus(WorkflowStatus.PENDING.getStatus());
			entity.setCardStatus(WorkflowStatus.PENDING.getStatus());
			entity.onCreate();
			tempAccomPaymentAdviceRepository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Transactional
	public boolean deletePaymentAdvice(Long id) throws Exception {
		try {
			tempAccomPaymentAdviceRepository.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public TempAccomPaymentAdviceDto getPaymentDetails(Long candidateId, Long requestId, Long id) throws RecordListEmptyException {
		TempAccomPaymentAdviceDto dto = new TempAccomPaymentAdviceDto();
		dto.setCandidateId(candidateId);
		dto.setRequestId(requestId);
		dto.setId(id);
		fetchStayDetails(requestId, dto);
		fetchPaymentDetails(id, dto);
		fetchBalanceAmount(candidateId, dto);
		return dto;
	}

	private void fetchStayDetails(Long requestId, TempAccomPaymentAdviceDto dto) {
		Object[] stayDetails = temporaryAccomodationRepository.getStayDetails(requestId);
		if (stayDetails != null && stayDetails.length > 0) {
			Object[] data = (Object[]) stayDetails[0];
			dto.setRequestedStayFrom(data[0] != null ? ((java.sql.Date) data[0]).toLocalDate() : null);
			dto.setRequestedStayTo(data[1] != null ? ((java.sql.Date) data[1]).toLocalDate() : null);
		}
	}

	private void fetchPaymentDetails(Long id, TempAccomPaymentAdviceDto dto) {
		TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(id);
		dto.setHostelPayFromDate(entity.getHostelPayFromDate());
		dto.setHostelPayToDate(entity.getHostelPayToDate());
		dto.setMessPayFromDate(entity.getMessPayFromDate());
		dto.setMessPayToDate(entity.getMessPayToDate());
		dto.setOverallAmount(entity.getOverallAmount());
		dto.setPaymentAmount1(entity.getPaymentAmount1());
		dto.setPaymentType1(entity.getPaymentType1());
		dto.setPaymentReferenceNo1(entity.getPaymentReferenceNo1());
		dto.setPaymentDate1(entity.getPaymentDate1());
		dto.setPaymentAmount2(entity.getPaymentAmount2());
		dto.setPaymentType2(entity.getPaymentType2());
		dto.setPaymentReferenceNo2(entity.getPaymentReferenceNo2());
		dto.setPaymentDate2(entity.getPaymentDate2());
		dto.setPaymentStatus(entity.getPaymentStatus());
		if (entity.getHostelFloorMaster() != null && entity.getHostelFloorMaster().getHostel() != null) {
			dto.setHostelName(entity.getHostelFloorMaster().getHostel().getHostelName());
		}
		if (entity.getHostelRoomInfo() != null) {
			dto.setRoomNo(entity.getHostelRoomInfo().getRoomNo());
		}
		if (entity.getMessMaster() != null) {
			dto.setMessName(entity.getMessMaster().getMessName());
			dto.setMessId(entity.getMessMaster().getId());
			dto.setMessHead(entity.getMessMaster().getMessHead());
		}
		dto.setSeat(entity.getSeat());
		dto.setHostelTotalNoOfDays(entity.getHostelTotalNoOfDays());
	}

	private void fetchBalanceAmount(Long candidateId, TempAccomPaymentAdviceDto dto) {
		Long balanceAmount = temporaryAccomodationRepository.getBalanceAmount(candidateId,ModelConstants.STATUS_ACTIVE);
		dto.setBalanceAmount(balanceAmount);
	}

	@Transactional
	public boolean savePaymentDetails(TempAccomPaymentAdviceDto dto) throws Exception {
		try {
			TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(dto.getId());
			savePayment(entity, dto);
			String voucherNo = String.valueOf(accomodationLedgerARepository.getNextValAccomodationLedger());
			saveAccomodationLedgerA(entity, dto, voucherNo);
			saveAccomodationLedgerB(entity, dto, voucherNo);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}

	@Transactional
	public boolean updatePaymentDetails(TempAccomPaymentAdviceDto dto) throws Exception {
		try {
			TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(dto.getId());
			savePayment(entity, dto);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean savePayment(TempAccomPaymentAdviceEntity entity, TempAccomPaymentAdviceDto dto) throws Exception {
		entity.setActiveFlag(Constants.ACTIVE_FLAG);
		entity.setPaymentAmount1(dto.getPaymentAmount1());
		entity.setPaymentReferenceNo1(dto.getPaymentReferenceNo1());
		entity.setPaymentDate1(dto.getPaymentDate1());
		entity.setPaymentType1(dto.getPaymentType1());
		entity.setPaymentAmount2(dto.getPaymentAmount2());
		entity.setPaymentReferenceNo2(dto.getPaymentReferenceNo2());
		entity.setPaymentDate2(dto.getPaymentDate2());
		entity.setPaymentType2(dto.getPaymentType2());
		entity.setPaymentStatus(WorkflowStatus.COMPLETED.getStatus());
		entity.onUpdate();
		tempAccomPaymentAdviceRepository.save(entity);
		return true;
	}

	private void saveAccomodationLedgerA(TempAccomPaymentAdviceEntity entity, TempAccomPaymentAdviceDto dto, String voucherNo) throws Exception {
		AccomodationLedgerAEntity ledgerEntity = new AccomodationLedgerAEntity();
		AccomodationLedgerAEntityId ledgerEntityId = new AccomodationLedgerAEntityId();
		FinancialYearDto activeFinancialYear = financialYearService.getActiveFinancialYear();
		String description = Constants.ACCOMODATION_LEDGER_DESC1 + " " + entity.getPaymentType1() + " " + Constants.ON + " " + convertPaymentDate(entity.getPaymentDate1()) + " "
				+ Constants.ACCOMODATION_LEDGER_DESC2 + " " + entity.getPaymentReferenceNo1();
		ledgerEntityId.setFinYear(activeFinancialYear.getFinYear());
		ledgerEntityId.setVoucherNo(voucherNo);
		ledgerEntity.setId(ledgerEntityId);
		ledgerEntity.setVoucherDate(LocalDate.now());
		ledgerEntity.setCandidateId(dto.getCandidateId());
		ledgerEntity.setRequestId(dto.getRequestId());
		ledgerEntity.setDescription(description);
		ledgerEntity.setAmount(Double.valueOf(entity.getOverallAmount()));
		ledgerEntity.setPaymentRefId(entity.getId());
		ledgerEntity.setCancelStatus(Constants.CANCEL_STATUS);
		ledgerEntity.setDebitOrCredit(Constants.DEBIT);
		ledgerEntity.setActiveFlag(Constants.ACTIVE_FLAG);
		ledgerEntity.onCreate();
		accomodationLedgerARepository.save(ledgerEntity);
	}

	private String convertPaymentDate(LocalDateTime date) {
		return utility.convertToLocalDate(date).format(DateTimeFormatter.ofPattern(Constants.PAYMENT_DATE_FORMAT));
	}

	private void saveAccomodationLedgerB(TempAccomPaymentAdviceEntity entity, TempAccomPaymentAdviceDto dto, String voucherNo) throws Exception {
		List<Map.Entry<String, Double>> descriptionAmountList = new ArrayList<>();
		if (entity.getHostelPayFromDate() != null) {
			descriptionAmountList
					.add(Map.entry(Constants.ACCOMODATION_LEDGER_HOSTEL_AMOUNT + " " + entity.getPaymentType1(), Double.valueOf(entity.getHostelRatePerDay() * entity.getHostelTotalNoOfDays())));
		}
		if (entity.getMessPayFromDate() != null) {
			descriptionAmountList.add(Map.entry(Constants.ACCOMODATION_LEDGER_MESS_AMOUNT + " " + entity.getPaymentType1(),
					Double.valueOf(entity.getTotalBreakfastAmount() + entity.getTotalLunchAmount() + entity.getTotalDinnerAmount())));
		}
		if (entity.getCardStatus() != null && entity.getCardCharges() != null) {
			descriptionAmountList.add(Map.entry(Constants.ACCOMODATION_LEDGER_CARD_CHARGES + " " + entity.getPaymentType1(), Double.valueOf(entity.getCardCharges())));
		}
		FinancialYearDto activeFinancialYear = financialYearService.getActiveFinancialYear();
		int count = 0;
		for (Map.Entry<String, Double> entry : descriptionAmountList) {
			String description = entry.getKey();
			Double amount = entry.getValue();

			AccomodationLedgerBEntityId ledgerEntityId = new AccomodationLedgerBEntityId();
			ledgerEntityId.setFinYear(activeFinancialYear.getFinYear());
			ledgerEntityId.setSlNo(count++);
			ledgerEntityId.setVoucherNo(voucherNo);

			AccomodationLedgerBEntity ledgerEntity = new AccomodationLedgerBEntity();
			ledgerEntity.setId(ledgerEntityId);
			ledgerEntity.setVoucherDate(LocalDate.now());
			ledgerEntity.setCandidateId(dto.getCandidateId());
			ledgerEntity.setRequestId(dto.getRequestId());
			ledgerEntity.setDescription(description);
			ledgerEntity.setAmount(amount);
			ledgerEntity.setPaymentRefId(entity.getId());
			ledgerEntity.setCancelStatus(Constants.CANCEL_STATUS);
			ledgerEntity.setDebitOrCredit(Constants.DEBIT);
			ledgerEntity.setActiveFlag(Constants.ACTIVE_FLAG);
			ledgerEntity.onCreate();
			accomodationLedgerBRepository.save(ledgerEntity);
		}
	}

	public int checkReferenceNumber(String referenceNumber) {
		int count = temporaryAccomodationRepository.checkReferenceNumber(referenceNumber);
		return count;
	}

	public int checkDates(TempAccomPaymentAdviceDto dto) {
		int hostelCount = temporaryAccomodationRepository.checkAccomodationDates(dto.getRequestId(), dto.getHostelPayFromDate(), dto.getHostelPayToDate());
		int messCount = temporaryAccomodationRepository.checkMessDates(dto.getRequestId(), dto.getMessPayFromDate(), dto.getMessPayToDate());
		return hostelCount + messCount;
	}

	@Transactional
	public boolean approvePaymentAdvice(Long id) throws Exception {
		try {
			TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(id);
			entity.setPaymentApprovalStatus(WorkflowStatus.APPROVED.getStatus());
			entity.setPaymentApprovalDate(LocalDateTime.now());
			entity.onUpdate();
			tempAccomPaymentAdviceRepository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private List<String> getStayStatusList() {
		return List.of(WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.ALLOTTED.getStatus(), WorkflowStatus.CHECKED_IN.getStatus(), WorkflowStatus.CHECKED_OUT.getStatus(),
				WorkflowStatus.CLOSED.getStatus(), "-", WorkflowStatus.REJECTED.getStatus());
	}

	private List<String> getAppStatusList() {
		return List.of(WorkflowStatus.APPROVED.getStatus(), WorkflowStatus.ALLOTTED.getStatus(), WorkflowStatus.CHECKED_IN.getStatus(), WorkflowStatus.CHECKED_OUT.getStatus());
	}

	public List<HostelMasterDto> getHostelList() {
		return hostelMasterService.getHostelList();
	}

	public Map<Long, String> getRoomList(Long requestId, Long tempAccomPaymentAdviceId, Long hostelId) {
		Object[] roomDetails = temporaryAccomodationRepository.getRoomList(requestId, tempAccomPaymentAdviceId, hostelId, false, null);
		Map<Long, String> availableRooms = new HashMap<>();
		if (roomDetails != null && roomDetails.length > 0) {
			for (Object data : roomDetails) {
				Object[] roomData = (Object[]) data;
				Long roomId = utility.parseLong(roomData[0]);
				String roomNo = ValidationCommon.toString(roomData[1]);
				int remainingCount = utility.parseInt(roomData[3]);
				if (remainingCount > 0) {
					availableRooms.put(roomId, roomNo);
				}
			}
		}
		return availableRooms;
	}

	public List<String> getSeatList(Long requestId, Long tempAccomPaymentAdviceId, Long hostelId, String roomNo) {
		Object[] seatDetails = temporaryAccomodationRepository.getRoomList(requestId, tempAccomPaymentAdviceId, hostelId, true, roomNo);
		List<String> availableSeats = new ArrayList<>();
		if (seatDetails != null && seatDetails.length > 0) {
			Object[] data = (Object[]) seatDetails[0];
			int totalCapacity = utility.parseInt(data[2]);
			int remainingCapacity = utility.parseInt(data[3]);
			String subRoomList = ValidationCommon.toString(data[4]);
			if (subRoomList == null) {
				subRoomList = "";
			}
			String[] subRoomArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

			int count = 1;
			for (int i = 0; i < totalCapacity; i++) {
				if (!subRoomList.contains(subRoomArray[i])) {
					availableSeats.add(subRoomArray[i]);
					count++;
					if (count > remainingCapacity) {
						break;
					}
				}
			}
		}
		return availableSeats;
	}

	public boolean saveHostelDetails(TempAccomPaymentAdviceDto dto) {
		try {
			TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(dto.getId());
            HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(Math.toIntExact(dto.getRoomId()));
			entity.setHostelFloorMaster(hostelFloorMasterRepository.getReferenceById(hostelRoomInfoDto.getBuilding().getId()));
			entity.setHostelRoomInfo(hostelRoomInfoRepository.getReferenceById(dto.getRoomId()));
			entity.setSeat(dto.getSeat());
			entity.onUpdate();
			tempAccomPaymentAdviceRepository.save(entity);
            saveVacationRoomInfoTable(dto, entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    private void saveVacationRoomInfoTable(TempAccomPaymentAdviceDto dto, TempAccomPaymentAdviceEntity entity) {
        CandidateProfileDto candidateProfileDto = candidateProfileRepository
                .findByIdAndActiveFlag(dto.getCandidateId(), ModelConstants.STATUS_ACTIVE).map(CandidateProfileMapper.INSTANCE::toDto).orElse(null);
        VacationHostelRoomAllotmentInfoEntity vacationHostelRoomAllotmentInfoEntity = vacationHostelRoomAllotmentInfoRepository
                .getAllocatedDetailsByRequestId(entity.getHostelPayFromDate(), entity.getHostelPayToDate(), Objects.requireNonNull(candidateProfileDto).getEmail(),
                        dto.getRequestId(), ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (!Objects.nonNull(vacationHostelRoomAllotmentInfoEntity)) {
            HostelRoomInfoEntity hostelRoomInfoEntity = HostelRoomInfoMapper.INSTANCE.toHostelRoomInfoEntity(hostelRoomInfoService.getHostelRoomInfoDetailsById(Math.toIntExact(dto.getRoomId())));
            vacationHostelRoomAllotmentInfoEntity = new VacationHostelRoomAllotmentInfoEntity();
            vacationHostelRoomAllotmentInfoEntity.setRequestid(String.valueOf(dto.getRequestId()));
            vacationHostelRoomAllotmentInfoEntity.setEmail(Objects.requireNonNull(candidateProfileDto).getEmail());
            vacationHostelRoomAllotmentInfoEntity.setGender(candidateProfileDto.getGender());
            vacationHostelRoomAllotmentInfoEntity.setStayFromDate(entity.getHostelPayFromDate());
            vacationHostelRoomAllotmentInfoEntity.setStayToDate(entity.getHostelPayToDate());
            vacationHostelRoomAllotmentInfoEntity.setRoomid(hostelRoomInfoEntity);
            vacationHostelRoomAllotmentInfoEntity.setBuilding(hostelRoomInfoEntity.getBuilding());
            vacationHostelRoomAllotmentInfoEntity.setSubRoomid(dto.getSeat());
            vacationHostelRoomAllotmentInfoEntity.setStayId(String.valueOf(0L));
            vacationHostelRoomAllotmentInfoRepository.saveAndFlush(vacationHostelRoomAllotmentInfoEntity);
        }
    }

    public boolean changeMess(TempAccomPaymentAdviceDto dto) {
		try {
			TempAccomPaymentAdviceEntity entity = getPaymentAdviceEntity(dto.getId());
			entity.setMessMaster(messMasterRepository.getReferenceById(dto.getMessId()));
			entity.onUpdate();
			tempAccomPaymentAdviceRepository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public byte[] generatePdf(Long candidateId, Long requestId, Long paymentAdviceId) throws Exception {
		TemporaryAccomodationDto temporaryAccomodationDto = new TemporaryAccomodationDto();
		fetchCandidateDetails(candidateId, temporaryAccomodationDto);
		fetchAppointmentDetails(candidateId, requestId, temporaryAccomodationDto);
		TempAccomPaymentAdviceDto tempAccomPaymentAdviceDto = new TempAccomPaymentAdviceDto();
		tempAccomPaymentAdviceDto.setId(paymentAdviceId);
		fetchPaymentDetails(paymentAdviceId, tempAccomPaymentAdviceDto);
		return tempAccommDetailsPDF(temporaryAccomodationDto, tempAccomPaymentAdviceDto);
	}

	public byte[] tempAccommDetailsPDF(TemporaryAccomodationDto temporaryAccomodationDto, TempAccomPaymentAdviceDto tempAccomPaymentAdviceDto) throws Exception {
		byte[] byteVal;

		try (ByteArrayOutputStream outfile = new ByteArrayOutputStream()) {
			int marginBottom = 5;
			int normalFontSize = 11;
			int boldFontSize = 10;

			PdfWriter writer = new PdfWriter(outfile);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A4.rotate());

			String[] copies = { messageSource.getMessage("message.label.office.copy", null, Locale.getDefault()),
					messageSource.getMessage("message.label.caterer.copy", null, Locale.getDefault()),
					messageSource.getMessage("message.label.hostel.office.copy", null, Locale.getDefault()),
					messageSource.getMessage("message.label.students.copy", null, Locale.getDefault()) };

			//float[] columnWidths = { 25, 25, 25, 25 };
			float[] columnWidths = { 23, 2.5f, 23, 2.5f, 23, 2.5f, 23 };
			Table table = new Table(columnWidths);
			table.setWidth(UnitValue.createPercentValue(100));

			document.setMargins(30, 15, 20, 15);
			table.setWidth(UnitValue.createPercentValue(100));

			PdfFont labelFont = PdfFontFactory.createFont("Times-Roman");
			PdfFont boldFont = PdfFontFactory.createFont("Times-Bold");

			for (int i = 0; i < copies.length; i++) {
				String copy = copies[i];

				Cell cell = new Cell();
				cell.setPadding(5);
				cell.setBorder(new SolidBorder(1));
				float COPY_HEIGHT = 520f;
				cell.setHeight(COPY_HEIGHT);
				cell.setMaxHeight(COPY_HEIGHT);
				cell.setKeepTogether(true);
				cell.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

				float[] nestedColumnWidths = { 1, 3 };
				Table nestedTable = new Table(nestedColumnWidths);
				nestedTable.setMarginTop(10);
				nestedTable.setWidth(UnitValue.createPercentValue(100));

				ClassLoader classLoader = getClass().getClassLoader();
				InputStream imageStream = classLoader.getResourceAsStream(simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO));

				if (imageStream != null) {
					ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
					Image headerLogo = new Image(imageData);
					headerLogo.setHeight(25);
					headerLogo.setWidth(25);
					nestedTable.addCell(new Cell().add(headerLogo).setBorder(Border.NO_BORDER));
				}

				nestedTable.addCell(new Cell().add(new Paragraph(copy)
						.setFont(boldFont).setFontSize(boldFontSize)
						.setTextAlignment(TextAlignment.JUSTIFIED)
						.setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setMarginBottom(marginBottom))
						.setBorder(Border.NO_BORDER));

				cell.add(nestedTable);

				cell.add(new Paragraph(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()))
						.setFont(boldFont).setFontSize(boldFontSize)
						.setTextAlignment(TextAlignment.CENTER).setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.temporary.slip", null, Locale.getDefault()))
						.setFont(boldFont).setFontSize(boldFontSize)
						.setTextAlignment(TextAlignment.CENTER).setMarginBottom(marginBottom));

				float[] nestedColumnWidthsForCell = { 1, 1 };
				Table nestedTableForCell = new Table(nestedColumnWidthsForCell);
				nestedTableForCell.setWidth(UnitValue.createPercentValue(100));
				nestedTableForCell.addCell(new Cell()
						.add(new Paragraph(messageSource.getMessage("message.label.s.no", null, Locale.getDefault()) + " : " + tempAccomPaymentAdviceDto.getId())
								.setFont(boldFont)
								.setFontSize(boldFontSize)
								.setTextAlignment(TextAlignment.LEFT)
								.setMarginBottom(marginBottom))
								.setBorder(Border.NO_BORDER));
				nestedTableForCell.addCell(
						new Cell().add(new Paragraph(
							    messageSource.getMessage("message.label.date", null, Locale.getDefault()) + " : " +
							    LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
							)
							.setFont(boldFont)
							.setFontSize(boldFontSize)
							.setTextAlignment(TextAlignment.LEFT)
							.setMarginBottom(marginBottom))
							.setBorder(Border.NO_BORDER));
				cell.add(nestedTableForCell);

				String name = limitText(temporaryAccomodationDto.getFirstName() + ModelConstants.SPACE + temporaryAccomodationDto.getLastName(), 25, 3);
				cell.add(new Paragraph(commonResponseUtil.getMessage("message.label.name") + " : " + name)
						.setFont(labelFont)
						.setFontSize(getFontSize(name))
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				String email = limitText(temporaryAccomodationDto.getEmail(), 25, 3);
				cell.add(new Paragraph(commonResponseUtil.getMessage("message.label.email") + " : " + email)
						.setFont(labelFont)
						.setFontSize(getFontSize(email))
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				String category = limitText(temporaryAccomodationDto.getCandidateCategory(), 25, 3);
				cell.add(new Paragraph(commonResponseUtil.getMessage("message.label.category") + " : " + category)
						.setFont(labelFont)
						.setFontSize(getFontSize(category))
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(commonResponseUtil.getMessage("message.label.no.of.person") + " : " + "1")
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

				// Format hostelPayFromDate
				String formattedFrom = (tempAccomPaymentAdviceDto.getHostelPayFromDate() != null)
				    ? tempAccomPaymentAdviceDto.getHostelPayFromDate().format(outputFormatter)
				    : "";

				// Format hostelPayToDate
				String formattedTo = (tempAccomPaymentAdviceDto.getHostelPayToDate() != null)
				    ? tempAccomPaymentAdviceDto.getHostelPayToDate().format(outputFormatter)
				    : "";

				String stayPeriod = messageSource.getMessage("message.label.stay.period", null, Locale.getDefault())
				                   + " : " + formattedFrom + " to " + formattedTo;

				cell.add(new Paragraph(stayPeriod)
				    .setFont(labelFont)
				    .setFontSize(normalFontSize)
				    .setTextAlignment(TextAlignment.LEFT)
				    .setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.no.of.days", null, Locale.getDefault()) + " : " +
						(tempAccomPaymentAdviceDto.getHostelTotalNoOfDays() != null ? tempAccomPaymentAdviceDto.getHostelTotalNoOfDays() : ""))
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.signature.candidate", null, Locale.getDefault()))
						.setFont(boldFont).setFontSize(boldFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.office.use", null, Locale.getDefault()))
						.setFont(boldFont)
						.setFontSize(boldFontSize)
						.setTextAlignment(TextAlignment.CENTER)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.hostel.room.no", null, Locale.getDefault()) + " : " +
							ValidationCommon.toString(tempAccomPaymentAdviceDto.getHostelName()) + "/" + ValidationCommon.toString(tempAccomPaymentAdviceDto.getRoomNo()))
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				//cell.add(new Paragraph(messageSource.getMessage("message.label.caterer.attached", null, Locale.getDefault()) + " : " + ValidationCommon.toString(tempAccomPaymentAdviceDto.getMessHead()))
						//.setFont(labelFont)
						//.setFontSize(normalFontSize)
						//.setTextAlignment(TextAlignment.LEFT)
						//.setMarginBottom(marginBottom));

				DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");

				String messHead = ValidationCommon.toString(tempAccomPaymentAdviceDto.getMessHead());

				String fromDateStr = (tempAccomPaymentAdviceDto.getMessPayFromDate() != null)
				    ? tempAccomPaymentAdviceDto.getMessPayFromDate().format(shortDateFormatter)
				    : "";

				String toDateStr = (tempAccomPaymentAdviceDto.getMessPayToDate() != null)
				    ? tempAccomPaymentAdviceDto.getMessPayToDate().format(shortDateFormatter)
				    : "";

				String dateRange = "";
				if (!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
				    dateRange =  fromDateStr + " to " + toDateStr;
				}

				cell.add(new Paragraph(
						messageSource.getMessage("message.label.caterer.attached", null, Locale.getDefault())
				        + " : " + messHead )

				    .setFont(labelFont)
				    .setFontSize(normalFontSize)
				    .setTextAlignment(TextAlignment.LEFT)
				    .setMarginBottom(marginBottom));

				cell.add(new Paragraph(
				        messageSource.getMessage("message.label.pdf.mess.period", null, Locale.getDefault())
				        + " : " + dateRange)

				    .setFont(labelFont)
				    .setFontSize(normalFontSize)
				    .setTextAlignment(TextAlignment.LEFT)
				    .setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.total.amount.payment.rs", null, Locale.getDefault())  + tempAccomPaymentAdviceDto.getOverallAmount())
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.mode.of.payment", null, Locale.getDefault()) + " : "
							+ tempAccomPaymentAdviceDto.getPaymentType1() + "/"
							+ tempAccomPaymentAdviceDto.getPaymentReferenceNo1().toUpperCase()
							+ "/"
							+ tempAccomPaymentAdviceDto.getPaymentAmount1())
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				if(StringUtils.isNotEmpty(tempAccomPaymentAdviceDto.getPaymentType2()) && StringUtils.isNotEmpty(tempAccomPaymentAdviceDto.getPaymentReferenceNo2()) ){
					cell.add(new Paragraph(messageSource.getMessage("message.label.mode.of.secondary.payment", null, Locale.getDefault()) + " : "
								+ tempAccomPaymentAdviceDto.getPaymentType2() + "/"
								+ tempAccomPaymentAdviceDto.getPaymentReferenceNo2().toUpperCase()
								+ "/"
								+ tempAccomPaymentAdviceDto.getPaymentAmount2())
							.setFont(labelFont)
							.setFontSize(normalFontSize)
							.setTextAlignment(TextAlignment.LEFT)
							.setMarginBottom(marginBottom));
				}

				cell.add(new Paragraph(messageSource.getMessage("message.label.card.no", null, Locale.getDefault()) + " :  -")
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.LEFT)
						.setMarginBottom(marginBottom));

				cell.add(new Paragraph(messageSource.getMessage("message.label.for.ccw", null, Locale.getDefault()))
						.setFont(labelFont)
						.setFontSize(normalFontSize)
						.setTextAlignment(TextAlignment.RIGHT)
						.setMarginBottom(marginBottom));

				cell.setBorder(new SolidBorder(1));
				table.addCell(cell);

				// ===== TEAR LINE COLUMN =====
				if (i < copies.length - 1) {
					Cell separator = new Cell();

				    // solid vertical cut line
				    separator.setBorderLeft(new SolidBorder(1));

				    // spacing around line
				    separator.setPaddingLeft(4);
				    separator.setPaddingRight(4);

				    // no other borders
				    separator.setBorderRight(Border.NO_BORDER);
				    separator.setBorderTop(Border.NO_BORDER);
				    separator.setBorderBottom(Border.NO_BORDER);

				    table.addCell(separator);
				}

			}

			document.add(table);
			document.close();
			byteVal = outfile.toByteArray();
		}

		return byteVal;
	}

	private TempAccomPaymentAdviceEntity getPaymentAdviceEntity(Long id) {
		Optional<TempAccomPaymentAdviceEntity> optionalEntity = tempAccomPaymentAdviceRepository.findById(id);
		if (optionalEntity.isPresent()) {
			return optionalEntity.get();
		} else {
			throw new EntityNotFoundException(messageSource.getMessage("message.payment.advice.record.not.found", null, Locale.getDefault()) + " " + id);
		}
	}

	private String encryptAccommodationRequestUrl(Long candidateId, Long requestId, Long paymentAdviceId) throws Exception {
		String encryptKey = null;
		if (paymentAdviceId != null) {
			encryptKey = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + paymentAdviceId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
		} else {
			encryptKey = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
		}
		return MCrypt.getInstance().encryptToText(encryptKey);
	}

	private String limitText(String text, int maxCharsPerLine, int maxLines) {
		if (text == null) return "";
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		for (int line = 0; line < maxLines && idx < text.length(); line++) {
			int end = Math.min(idx + maxCharsPerLine, text.length());
			sb.append(text, idx, end);
			idx = end;

			if (line < maxLines - 1 && idx < text.length()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private float getFontSize(String text) {
		if (text == null) {
			return 11;
		}
		if (text.length() > 35) {
			return 8;
		}
		if (text.length() > 25) {
			return 9;
		}
		return 11;
	}

}
