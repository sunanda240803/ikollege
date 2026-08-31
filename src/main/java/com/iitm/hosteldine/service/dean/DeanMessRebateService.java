package com.iitm.hosteldine.service.dean;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.dto.dean.DeanMessRebateWorkflowDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessRebateEntity;
import com.iitm.hosteldine.model.mess.MessRebateWorkflowEntity;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.mess.MessRebateRepository;
import com.iitm.hosteldine.repository.mess.MessRebateWorkflowRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.helper.MessRebateServiceHelper;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeanMessRebateService {

	private static final String USER_ROLE_CCW = "CCW Dean"; // Need to remove this line

	private final MessRebateRepository messRebateRepository;
	private final DynamicUserTabRepository dynamicUserTabRepository;
	private final Utility utility;
	private final MessageSource messageSource;
	private final MessRebateWorkflowRepository messRebateWorkflowRepository;
	private final MessRebateServiceHelper messRebateServiceHelper;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;

	public List<DeanMessRebateDto> getMessRebateList(PaginationForm form, String url, boolean isExport) {
		Object[] result = fetchMessRebateList(form, isExport);
		List<Object[]> subMenuList = dynamicUserTabRepository.getDeanSubMenuListById(url, SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());
		return setMessRebateListValues(url, result, subMenuList);
	}

	private Object[] fetchMessRebateList(PaginationForm form, boolean isExport) {
		String validationStatus = ValidationCommon.toString(form.getAdditionalParam().get("validationStatus"));
		LocalDate approvalFromDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("approvalFromDate"));
		LocalDate approvalToDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("approvalToDate"));
		LocalDate submittedFromDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedFromDate"));
		LocalDate submittedToDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedToDate"));
		LocalDate rebateFromDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("rebateFromDate"));
		LocalDate rebateToDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("rebateToDate"));
		String studentName = ValidationCommon.toString(form.getAdditionalParam().get("studentName"));
		String studentId = ValidationCommon.toString(form.getAdditionalParam().get("studentId"));
		String hodName = ValidationCommon.toString(form.getAdditionalParam().get("hodName"));
		String hodEmail = ValidationCommon.toString(form.getAdditionalParam().get("requestedStatus"));
		String siNoFrom = ValidationCommon.toString(form.getAdditionalParam().get("siNoFrom"));
		String siNoTo = ValidationCommon.toString(form.getAdditionalParam().get("siNoTo"));
		String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
		String userName = Objects.requireNonNull(SecurityCtxUtil.userName());
//		userRole = USER_ROLE_CCW; // Need to remove

		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Object[] result;

		if (isExport) {
			pageable = Pageable.unpaged();
		}
		
		result = messRebateRepository.getMessApprovalListByFilter(getStatus(validationStatus , userRole), approvalFromDate, approvalToDate, submittedFromDate, submittedToDate, rebateFromDate, rebateToDate, studentName,
				studentId, hodName, hodEmail, getAuthorityType(userRole), siNoFrom, siNoTo, userRole, userName);
		
		return result;
	}

	private String getStatus(String validationStatus, String userRole) {
		if(userRole != null && userRole.contains(Constants.ROLE_OFFICE)) {
			return WorkflowStatus.APPROVED.getStatus();
		} else {
			return validationStatus;
		}
	}

	private String getAuthorityType(String role) {
		if (RoleEnum.DOST_DEAN.getValue().equalsIgnoreCase(role)) {
			return RoleEnum.DOST_DEAN.getValue();
		} else if (RoleEnum.ICSR_DEAN.getValue().equalsIgnoreCase(role)) {
			return RoleEnum.ICSR_DEAN.getValue();
		} else if (RoleEnum.CCW_DEAN.getValue().equalsIgnoreCase(role)) {
			return RoleEnum.CCW_DEAN.getValue();
		} else {
			return RoleEnum.DEAN.getValue();
		}
	}

	private List<DeanMessRebateDto> setMessRebateListValues(String url, Object[] result, List<Object[]> subMenuList) {
		return Arrays.stream(result).map(data -> {
			Object[] objects = (Object[]) data;
			DeanMessRebateDto dto = new DeanMessRebateDto();
			List<PropertyDto> actionList = new ArrayList<PropertyDto>();
			createActionList(url, subMenuList, objects, actionList);
			dto.setSlNo(((Long) objects[9]));
			dto.setCreatedAt(objects[0] != null ? (DateUtility.toLocalDateTime(objects[0]).toLocalDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);
			dto.setStudentId(ValidationCommon.toString(objects[1]));
			dto.setFromDateLeave(objects[2] != null ? utility.convertToLocalDate(objects[2]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)) : null);
			dto.setToDateLeave(objects[3] != null ? utility.convertToLocalDate(objects[3]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)) : null);
			dto.setFromDateRebate(objects[4] != null ? utility.convertToLocalDate(objects[4]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)) : null);
			dto.setToDateRebate(objects[5] != null ? utility.convertToLocalDate(objects[5]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)) : null);
			dto.setNoOfDays((int) objects[6]);
			dto.setDescription(ValidationCommon.toString(objects[7]));
			dto.setStatus(ValidationCommon.toString(objects[8]));
			dto.setGuideName(ValidationCommon.toString(objects[10]));
			dto.setGuideApprovalStatus(ValidationCommon.toString(objects[11]));
			dto.setGuideApprovalDate(ValidationCommon.formatDate(objects[12], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setCcwName(ValidationCommon.toString(objects[13]));
			dto.setCcwApprovalStatus(ValidationCommon.toString(objects[14]));
			dto.setCcwApprovalDate(ValidationCommon.formatDate(objects[15], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setActionList(actionList);
			return dto;
		}).toList();
	}

	private void createActionList(String url, List<Object[]> subMenuList, Object[] objects, List<PropertyDto> actionList) {
		for (Object[] action : subMenuList) {
			if (action[2].toString().equals(Constants.COL_LINK) || action[2].toString().equals(Constants.COL_ACTION)) {
				if (objects[8] != null && !objects[8].equals("")) {
					PropertyDto actionDto = createPropertyDto(action);
					String status = objects[8].toString();
					String authorityType = Objects.requireNonNull(SecurityCtxUtil.userRole());
					String studentIdVal = objects[1] != null ? objects[1].toString() : null;
					Long requestId = objects[9] != null ? ((Long) objects[9]) : null;
					try {
						if (WorkflowStatus.VIEW.getStatus().equals(action[8].toString())) {
							actionDto.setUrl(url + "/view?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
							actionList.add(actionDto);
						} else if (WorkflowStatus.RESEND_MAIL.getStatus().equals(action[8].toString())) {
							if (WorkflowStatus.PENDING.getStatus().equals(status) || WorkflowStatus.VALIDATING.getStatus().equals(status)) {
								actionDto.setUrl(url + "/resendMail?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, null));
								actionList.add(actionDto);
							}
						} else if (WorkflowStatus.DELETE.getStatus().equals(action[8].toString())) {
							if (WorkflowStatus.PENDING.getStatus().equals(status) || WorkflowStatus.VALIDATING.getStatus().equals(status) || WorkflowStatus.DEFAULT.getStatus().equals(status)) {
								actionDto.setUrl(url + "/delete?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
								actionList.add(actionDto);
							}
						} else if (WorkflowStatus.APPROVE.getStatus().equals(action[8].toString())) {
							if (WorkflowStatus.PENDING.getStatus().equals(status) || WorkflowStatus.VALIDATING.getStatus().equals(status) || WorkflowStatus.DEFAULT.getStatus().equals(status)) {
								actionDto.setUrl(url + "/approve?data=" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
								actionList.add(actionDto);
							}
						} else if (WorkflowStatus.CHECKBOX.getStatus().equals(action[8].toString()) &&
								(WorkflowStatus.PENDING.getStatus().equals(status) || WorkflowStatus.VALIDATING.getStatus().equals(status))) {
							actionDto.setUrl(url + commonResponseUtil.getMessage("url.bulk.approve.reject") + "/" + Utility.encryptAccommodationRequestUrl(studentIdVal, requestId, authorityType));
							actionList.add(actionDto);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public DeanMessRebateDto getMessRebateDetails(String studentId, Long id, String authorityType) throws Exception {
		// Need to remove this line
		if (Constants.USER_ROLE_DEAN.equalsIgnoreCase(authorityType)) {
			authorityType = USER_ROLE_CCW;
		}
		DeanMessRebateDto dto = new DeanMessRebateDto();
		messRebateServiceHelper.fetchStudentMessRebateDetails(studentId, id, dto);
		fetchMessRebateWorkFlowDetailsByAuthority(studentId, id, authorityType, dto);
		fetchMessRebateWorkFlowDetails(studentId, id, dto);
		return dto;
	}

	private void fetchMessRebateWorkFlowDetailsByAuthority(String studentId, Long id, String authorityType, DeanMessRebateDto dto) {
		Object[] messRebateWorkFlowDetailsByAuthority = messRebateRepository.getMessRebateWorkFlowDetailsByAuthority(studentId, id, authorityType);
		if (messRebateWorkFlowDetailsByAuthority != null && messRebateWorkFlowDetailsByAuthority.length > 0) {
			Object[] workflowData = (Object[]) messRebateWorkFlowDetailsByAuthority[0];
			dto.setWorkflowId((Long) workflowData[0]);
			dto.setWorkflowApprovalStatus(ValidationCommon.toString(workflowData[2]));
			dto.setWorkflowApprovalNotes(ValidationCommon.toString(workflowData[3]));
			dto.setWorkflowRejectReason(ValidationCommon.toString(workflowData[4]));
			dto.setApprovalLevel((int) workflowData[5]);
			dto.setAuthorityType(authorityType);
		}
	}

	private void fetchMessRebateWorkFlowDetails(String studentId, Long id, DeanMessRebateDto dto) {
		List<Object[]> messRebateWorkFlowDetails = messRebateRepository.getMessRebateWorkFlowDetails(studentId, id);
		dto.setDeanMessRebateWorkflowDtoList(getDeanMessRebateWorkflowDtoList(dto, messRebateWorkFlowDetails));
	}

	private List<DeanMessRebateWorkflowDto> getDeanMessRebateWorkflowDtoList(DeanMessRebateDto dto, List<Object[]> messRebateWorkFlowDetails) {
		List<DeanMessRebateWorkflowDto> deanMessRebateWorkflowDtoList = new ArrayList<>();
		StringBuilder approvalStatusDetails = new StringBuilder();
		dto.setTotalApprovalCount(messRebateWorkFlowDetails != null ? messRebateWorkFlowDetails.size() : 0);
		for (Object[] messRebateWorkFlow : messRebateWorkFlowDetails) {
			DeanMessRebateWorkflowDto deanMessRebateWorkflowDto = new DeanMessRebateWorkflowDto();
			deanMessRebateWorkflowDto.setApprovalNotes(ValidationCommon.toString(messRebateWorkFlow[7]));
			deanMessRebateWorkflowDto.setRejectionNotes(ValidationCommon.toString(messRebateWorkFlow[8]));
			String authType = ValidationCommon.toString(messRebateWorkFlow[4]);
			String approvalStatus = ValidationCommon.toString(messRebateWorkFlow[5]);
			// int approvalLevel = (int) messRebateWorkFlow[1];
			approvalStatusDetails.append(ValidationCommon.toString(messRebateWorkFlow[2]) + "   " + ValidationCommon.toString(messRebateWorkFlow[3]) + " - ");
			if (WorkflowStatus.APPROVED.getStatus().equals(approvalStatus) || WorkflowStatus.REJECTED.getStatus().equals(approvalStatus)) {
				String modifiedDate = messRebateWorkFlow[6] != null ? (utility.convertToLocalDate(messRebateWorkFlow[6]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : "";
				approvalStatusDetails.append(approvalStatus + " " + Constants.ON + " " + modifiedDate);
			} else if (ModelConstants.AUTHENTICATION_TYPE_INFORMATION.equalsIgnoreCase(authType) && WorkflowStatus.PENDING.getStatus().equals(approvalStatus)) {
				approvalStatusDetails.append(Constants.MESS_REBATE_INFORMATION);
			} else if (WorkflowStatus.DEFAULT.getStatus().equals(approvalStatus) || WorkflowStatus.PENDING.getStatus().equals(approvalStatus)) {
				approvalStatusDetails.append(WorkflowStatus.PENDING.getStatus());
			}
			approvalStatusDetails.append("<br>");
			deanMessRebateWorkflowDtoList.add(deanMessRebateWorkflowDto);
		}
		dto.setApprovalStatusDetails(approvalStatusDetails.toString());
		return deanMessRebateWorkflowDtoList;
	}

	@Transactional
	public boolean updateMessRebateStatusFromView(DeanMessRebateDto dto, String status, String url, HttpServletRequest request) {
		try {
			saveMessRebateStatus(dto, status);
			saveMessRebateWorkflowStatus(dto, status);
			if (dto.getApprovalLevel() != dto.getTotalApprovalCount()) {
				messRebateServiceHelper.sendMailToValidators(dto.getStudentId(), dto.getSlNo(), url, request);
			}
			messRebateServiceHelper.sendMailToStudent(dto.getStudentId(), dto.getSlNo(), url, dto.getWorkflowRejectReason());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void saveMessRebateStatus(DeanMessRebateDto dto, String status) throws Exception {
		try {
			MessRebateEntity messRebateEntity = messRebateRepository.findByIdAndActiveFlag(dto.getSlNo(), Constants.ACTIVE_FLAG);
			if (Objects.nonNull(dto.getFromDateRebate())) {
				LocalDate dtoFromDate = parseToLocalDate(dto.getFromDateRebate());
				LocalDate entityFromDate = messRebateEntity.getRebateFrom();
				if (!Objects.equals(dtoFromDate, entityFromDate)) {
					messRebateEntity.setRebateFrom(dtoFromDate);
				}
			}
			if (Objects.nonNull(dto.getToDateRebate())) {
				LocalDate dtoToDate = parseToLocalDate(dto.getToDateRebate());
				LocalDate entityToDate = messRebateEntity.getRebateTo();
				if (!Objects.equals(dtoToDate, entityToDate)) {
					messRebateEntity.setRebateTo(dtoToDate);
				}
			}
			if (!dto.getAuthorityType().toUpperCase().contains(Constants.CCW) && WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
				messRebateEntity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
			} else {
				messRebateEntity.setApprovalStatus(status);
				if(WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
					messRebateEntity.setApprovalDate(LocalDate.now());
				}
			}
			messRebateEntity.onUpdate();
			messRebateRepository.save(messRebateEntity);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private LocalDate parseToLocalDate(String dateStr) {
		if (dateStr == null || dateStr.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException ignored) {
		}
		try {
			return LocalDate.parse(dateStr, DateUtility.DATE_TIME_FORMATTER);
		} catch (DateTimeParseException ignored) {
		}
		throw new IllegalArgumentException("Unsupported date format: " + dateStr);
	}

	private void saveMessRebateWorkflowStatus(DeanMessRebateDto dto, String status) throws Exception {
		try {
			List<MessRebateWorkflowEntity> messRebateWorkflowEntityList = new ArrayList<>(
					messRebateWorkflowRepository.findByRequestIdAndStudentIdAndActiveFlag(dto.getSlNo(), dto.getStudentId(), Constants.ACTIVE_FLAG));

			// Sort the list based on the approval level
			messRebateWorkflowEntityList.sort(Comparator.comparingInt(MessRebateWorkflowEntity::getApprovalLevel));

			for (MessRebateWorkflowEntity messRebateWorkflowEntity : messRebateWorkflowEntityList) {
				if (dto.getApprovalLevel() == messRebateWorkflowEntity.getApprovalLevel()) {
					if (WorkflowStatus.APPROVED.getStatus().equals(status)) {
						messRebateWorkflowEntity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());
						messRebateWorkflowEntity.setApprovalNotes(dto.getWorkflowApprovalNotes());
						messRebateWorkflowEntity.setRejectionDescription(Strings.EMPTY);
						messRebateWorkflowEntity.onUpdate();
						messRebateWorkflowRepository.save(messRebateWorkflowEntity);
						
						// Update the next approval level to Pending
						Optional<Integer> nextApprovalLevel = messRebateWorkflowEntityList.stream()
								.filter(entity -> entity.getApprovalLevel() > dto.getApprovalLevel())
								.map(MessRebateWorkflowEntity::getApprovalLevel).findFirst();

						if (nextApprovalLevel.isPresent()) {
							int nextLevel = nextApprovalLevel.get();
							messRebateWorkflowEntityList.stream().filter(nextDto -> nextDto.getApprovalLevel() == nextLevel).forEach(nextEntity -> {
								nextEntity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
								nextEntity.onUpdate();
								messRebateWorkflowRepository.save(nextEntity);
							});
						} 
						
					} else if (WorkflowStatus.REJECTED.getStatus().equals(status)) {
						messRebateWorkflowEntity.setApprovalStatus(WorkflowStatus.REJECTED.getStatus());
						messRebateWorkflowEntity.setRejectionDescription(dto.getWorkflowRejectReason());
						messRebateWorkflowEntity.setApprovalNotes(Strings.EMPTY);
						messRebateWorkflowEntity.onUpdate();
						messRebateWorkflowRepository.save(messRebateWorkflowEntity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Transactional
	public boolean updateMessRebateStatus(String studentId, Long requestId, String status, String url) {
		try {
			String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
			userRole = USER_ROLE_CCW; // Need to remove this line
			MessRebateEntity messRebateEntity = messRebateRepository.findByIdAndActiveFlag(requestId, Constants.ACTIVE_FLAG);
			if(WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
				messRebateEntity.setApprovalDate(LocalDate.now());
			}
			messRebateEntity.setApprovalStatus(status);

			Optional<MessRebateWorkflowEntity> optionalWorkflowEntity = messRebateWorkflowRepository.findByRequestIdAndStudentIdAndAuthorityTypeAndActiveFlag(requestId, studentId, userRole,
					Constants.ACTIVE_FLAG);
			if (optionalWorkflowEntity.isPresent()) {
				MessRebateWorkflowEntity messRebateWorkflowEntity = optionalWorkflowEntity.get();
				messRebateWorkflowEntity.setApprovalStatus(status);
				messRebateWorkflowEntity.onUpdate();
				messRebateWorkflowRepository.save(messRebateWorkflowEntity);
			}
			messRebateEntity.onUpdate();
			messRebateRepository.save(messRebateEntity);
			messRebateServiceHelper.sendMailToStudent(studentId, requestId, url, Strings.EMPTY);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public String resendMail(String studentId, Long requestId, String url, HttpServletRequest request) throws Exception {
		return messRebateServiceHelper.sendMailToValidators(studentId, requestId, url, request);
	}

	private PropertyDto createPropertyDto(Object[] action) {
		PropertyDto actionDto = new PropertyDto();
		actionDto.setActionIcon(action[5] != null ? action[5].toString() : null);
		actionDto.setActionStyle(action[6] != null ? action[6].toString() : null);
		actionDto.setDisplayName(action[8] != null ? action[8].toString() : null);
		return actionDto;
	}

	public Workbook getMessRebateReport(List<DeanMessRebateDto> messRebateDtoList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.mess.rebate.list", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);

			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(1);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.mess.rebate.list", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(2);
			SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
			String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
			excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(3);
			String[] headers = { messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()), messageSource.getMessage("message.label.studentID", null, Locale.getDefault()),
					messageSource.getMessage("message.label.rebate.from", null, Locale.getDefault()), messageSource.getMessage("message.label.rebate.to", null, Locale.getDefault()),
					messageSource.getMessage("message.label.no.of.days", null, Locale.getDefault()), messageSource.getMessage("message.label.description", null, Locale.getDefault()),
					messageSource.getMessage("message.label.submitted.date", null, Locale.getDefault()), messageSource.getMessage("message.label.guide.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.guide.approval.status", null, Locale.getDefault()),
					messageSource.getMessage("message.label.guide.approval.date", null, Locale.getDefault()), messageSource.getMessage("message.label.ccw.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.ccw.approval.status", null, Locale.getDefault()), messageSource.getMessage("message.label.ccw.approval.date", null, Locale.getDefault()) };

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 3;
			if (CollectionUtils.isNotEmpty(messRebateDtoList)) {
				for (DeanMessRebateDto messRebateDto : messRebateDtoList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, messRebateDto.getSlNo(), dataStyle);
					excelUtility.createCell(row, 1, messRebateDto.getStudentId(), dataStyle);
					excelUtility.createCell(row, 2, messRebateDto.getFromDateRebate(), dataStyle);
					excelUtility.createCell(row, 3, messRebateDto.getToDateRebate(), dataStyle);
					excelUtility.createCell(row, 4, messRebateDto.getNoOfDays(), dataStyle);
					excelUtility.createCell(row, 5, messRebateDto.getDescription(), dataStyle);
					excelUtility.createCell(row, 6, messRebateDto.getCreatedAt(), dataStyle);
					excelUtility.createCell(row, 7, messRebateDto.getGuideName(), dataStyle);
					excelUtility.createCell(row, 8, messRebateDto.getGuideApprovalStatus(), dataStyle);
					excelUtility.createCell(row, 9, messRebateDto.getGuideApprovalDate(), dataStyle);
					excelUtility.createCell(row, 10, messRebateDto.getCcwName(), dataStyle);
					excelUtility.createCell(row, 11, messRebateDto.getCcwApprovalStatus(), dataStyle);
					excelUtility.createCell(row, 12, messRebateDto.getCcwApprovalDate(), dataStyle);
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating Mess Rebate List report", exception);
		}
		return workbook;
	}
	
	public String[] getValidationStatusList(String validationStatus) {
        List<String> validationStatusList = simsConfigDataService.getSimConfigValueArrayList(validationStatus)
                .stream()
                .filter(status -> !List.of("CheckedIn", "CheckedOut", "Validating").contains(status))
                .toList();
        return validationStatusList.toArray(new String[0]);
    }

}
