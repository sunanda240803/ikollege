package com.iitm.hosteldine.service.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.*;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.bulkappointment.StudentBulkAppointmentDetailsEntity;
import com.iitm.hosteldine.model.bulkappointment.StudentBulkAppointmentEntity;
import com.iitm.hosteldine.model.bulkappointment.StudentMasterBulkAppointmentEntity;
import com.iitm.hosteldine.model.staff.StaffDetailsEntity;
import com.iitm.hosteldine.repository.bulkappointment.StudentBulkAppointmentDetailsRepository;
import com.iitm.hosteldine.repository.bulkappointment.StudentMasterBulkAppointmentRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentBlackListDetailRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessRebateRepository;
import com.iitm.hosteldine.repository.staff.StaffDetailsRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StudentBulkAccommodationService {
	
	private final MessRebateRepository messRebateRepository;
	private final StudentMasterBulkAppointmentRepository studentMasterBulkAppointmentRepository;
	private final StudentBulkAppointmentDetailsRepository studentBulkAppointmentDetailsRepository;
	private final DynamicUserTabRepository dynamicUserTabRepository;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final ExcelUtility excelUtility;
	private final StudentBlackListDetailRepository studentBlackListDetailRepository;
	private final MailTemplateRepository mailTemplateRepository;
	private final MessageSource messageSource;
	private final MailQueueService mailQueueService;
	private final StaffDetailsRepository staffDetailsRepository;

	static boolean isFacultyLoggedin = false;
    private final CommonResponseUtil commonResponseUtil;

    @Value("${url.public.api}")
	private String publicApiUrl;

	@Value("${url.dean.student.bulk.accommodation.edit}")
	private String editStudentBulkUploadUrl;

	@Value("${url.dean.student.bulk.accommodation.view}")
	private String viewStudentBulkUploadUrl;

	@Value("${url.dean.student.bulk.accommodation.view.and.update.status}")
	private String viewAndUpdateStatusStudentBulkUploadUrl;

	@Value("${url.download.student.bulk.accommodation.file.download}")
	private String downloadFileLink;


	public List<StudentAccomBulkRequestDto> getBulkUploadList(PaginationForm form, String value, Boolean isFaculty, DeanApprovalDto columnList) {
		isFacultyLoggedin = isFaculty;
		String userName = SecurityCtxUtil.userName();
		String userRole = SecurityCtxUtil.userRole();

		//Updating additional param values
		String approvalStatus = (form.getAdditionalParam().get("validationStatus")!=null && !form.getAdditionalParam().get("validationStatus").equals("")) ? form.getAdditionalParam().get("validationStatus").toString() : null;
		LocalDate fromDate = (form.getAdditionalParam().get("approvalFromDate")!=null && !form.getAdditionalParam().get("approvalFromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("approvalFromDate").toString()) : null;
		LocalDate toDate = (form.getAdditionalParam().get("approvalToDate")!=null && !form.getAdditionalParam().get("approvalToDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("approvalToDate").toString()) : null;
		LocalDate createdDate = (form.getAdditionalParam().get("submittedFromDate")!=null && !form.getAdditionalParam().get("submittedFromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("submittedFromDate").toString()) : null;
		String fileName = (form.getAdditionalParam().get("fileName")!=null && !form.getAdditionalParam().get("fileName").equals(""))? form.getAdditionalParam().get("fileName").toString() : null;
		String eventName = (form.getAdditionalParam().get("eventName")!=null &&!form.getAdditionalParam().get("eventName").equals("")) ? form.getAdditionalParam().get("eventName").toString() : null;

		 int page = form.getPage() - 1;
		 Pageable pageable = PageRequest.of(page, form.getSize());
		 List<StudentAccomBulkRequestDto> result = new ArrayList<>();

		String loginId = SecurityCtxUtil.userId();
		result = studentMasterBulkAppointmentRepository.getFilteredBulkAppointments(
				 approvalStatus, eventName, toDate, fromDate , createdDate, loginId,userRole);

		result.forEach(dto -> {
			List<PropertyDto> actionList = columnList.getActionUrlList().stream()
					.filter(action -> shouldIncludeAction(action, dto, isFacultyLoggedin))
					.map(action -> createActionDto(action, value, dto.getBulkAppointmentId()))
					.collect(Collectors.toList());
			dto.setActionList(actionList);
		});
		return result;
	}



	private boolean shouldIncludeAction(PropertyDto action, StudentAccomBulkRequestDto dto, boolean isFacultyLoggedin) {
		if (dto.getApprovalStatus() == null) return false;

		String actionName = action.getDisplayName();
		String status = dto.getApprovalStatus();
		if (StringUtils.equalsAnyIgnoreCase(actionName,Constants.ACTION_VIEW)) return true;
		if (StringUtils.equalsAnyIgnoreCase(actionName,Constants.ACTION_DOWNLOAD) && !StringUtils.equalsAnyIgnoreCase(status,Constants.PENDING)) return true;
		if (isFacultyLoggedin) {
			 return !StringUtils.equalsAnyIgnoreCase(actionName, Constants.ACTION_APPROVE, Constants.ACTION_APPROVE_WITH_CONDITION,
					 Constants.ACTION_REJECT,Constants.ACTION_SEND_MESSAGE,Constants.ACTION_REJECT_REVERSAL);
		} else {

			if (Constants.ACTION_EDIT.equalsIgnoreCase(actionName)) return false;
			return switch (status) {
				case Constants.PENDING ->  StringUtils.equalsAnyIgnoreCase(actionName, Constants.ACTION_APPROVE, Constants.ACTION_APPROVE_WITH_CONDITION,
						Constants.ACTION_REJECT,Constants.ACTION_SEND_MESSAGE);
				case Constants.REJECTED-> Constants.ACTION_REJECT_REVERSAL.equalsIgnoreCase(actionName);
				default -> false;
			};
		}
	}

	private PropertyDto createActionDto(PropertyDto action, String basePath, Integer id) {
		PropertyDto dto = new PropertyDto();
		dto.setActionIcon(action.getActionIcon() != null ? action.getActionIcon() : null);
		dto.setActionStyle(action.getActionStyle() != null ? action.getActionStyle() : null);
		dto.setDisplayName(action.getDisplayName() != null ? action.getDisplayName() : null);
		String actionUrl = action.getUrl() != null ? action.getUrl() : null;
		dto.setUrl(basePath + (actionUrl != null ? "/" + actionUrl : "") + "/" + id);
		dto.setIsNoteRequired(true);
		return dto;
	}

	public Workbook downloadStudentBulkRequestUploadTemplate() {
		String sheetName = ExcelConstants.STUDENT_BULK_UPLOAD_SHEET_NAME;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount = 0;
		String[] headerData = isFacultyLoggedin ?  ExcelConstants.STUDENT_BULK_REQUEST_HEADER_DATA_FACULTY : ExcelConstants.STUDENT_BULK_REQUEST_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.STUDENT_BULK_REQUEST_HEADER_DATA_WIDTH;
		/**
		 * Data Style
		 */
		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);

		Row row0 = sheet.createRow(0);
		/**
		 * Set Header Lines
		 */
		excelUtility.createHeader(row0, columnCount, headerData, workbook);

		/**
		 * Set cell width
		 */
		IntStream.range(0, headerDataWidth.length).forEach(i -> {
			sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i]));

		});

		return workbook;
	}

	public StudentBulkAccommodationDto saveOrUpdateStudentBulkUpload(StudentBulkAccommodationDto studentBulkAccommodationDto, HttpServletRequest request) throws IOException {
		String userName = SecurityCtxUtil.userName();
		String userRole = SecurityCtxUtil.userRole();
		List<StudentBulkAccommodationDto> studentBulkAccommodationList = new ArrayList<>();
		StudentBulkAccommodationDto studentBulkInfoDto = new StudentBulkAccommodationDto();

		if(studentBulkAccommodationDto.getId() == null || (studentBulkAccommodationDto.getId() != null && studentBulkAccommodationDto.getFile() != null
				&& !studentBulkAccommodationDto.getFile().isEmpty() )){
			studentBulkInfoDto = saveStudentBulkUpload(studentBulkAccommodationDto, studentBulkAccommodationList);
		}
		if(CollectionUtils.isNotEmpty(studentBulkInfoDto.getErrorList())){
			return studentBulkInfoDto;
		}
		saveBulkAccommodation(studentBulkAccommodationList,studentBulkAccommodationDto, userName, userRole, request);
		return studentBulkInfoDto;
	}

	public StudentBulkAccommodationDto saveStudentBulkUpload(StudentBulkAccommodationDto studentBulkAccommodationDto,
															 List<StudentBulkAccommodationDto> studentBulkAccommodationList) throws IOException {



		DataFormatter formatter = new DataFormatter();
		StudentBulkAccommodationDto studentBulkInfoDto = new StudentBulkAccommodationDto();
		studentBulkInfoDto.setErrorList(new ArrayList<>());

		int rowNumber = 0;
		try (InputStream is = studentBulkAccommodationDto.getFile().getInputStream()) {
            Workbook workbook;
            try{
                workbook = new XSSFWorkbook(is);
            } catch (Exception e) {
                studentBulkInfoDto.getErrorList().add(commonResponseUtil.getMessage("message.validation.invalid.file.type"));
                return studentBulkInfoDto;
            }
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(rowNumber);
			List<String> headerList = isFacultyLoggedin ? Arrays.asList(ExcelConstants.STUDENT_BULK_REQUEST_HEADER_DATA_FACULTY) :
					Arrays.asList(ExcelConstants.STUDENT_BULK_REQUEST_HEADER_DATA);
			List<String> actualHeaders = new ArrayList<>();
			for (Cell cell : headerRow) {
				actualHeaders.add(formatter.formatCellValue(cell));
			}

			if (!new HashSet<>(headerList).containsAll(actualHeaders)) {
				studentBulkInfoDto.getErrorList().add(ExcelConstants.INVALID_EXCEL_TEMPLATE);
				return studentBulkInfoDto;
			}
			rowNumber++;

			// Check for empty file
			if (sheet.getPhysicalNumberOfRows() <= 1) {
				studentBulkInfoDto.getErrorList().add(ExcelConstants.EMPTY_FILE);
				return studentBulkInfoDto;
			}
			int rowCount = ExcelUtility.countNonEmptyRows(sheet);

			for (Row row : sheet) {
				if (rowCount > rowNumber) {
					if (row.getRowNum() == 0) { // Skip header row
						continue;
					}
					StudentBulkAccommodationDto studentAccommodation = new StudentBulkAccommodationDto();
					StringBuilder errorDetails = new StringBuilder();
					boolean error = false;
					int column = 0;

					// Student Id
					if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
						String studentId = row.getCell(column).toString().toUpperCase();
//						boolean isValidStudentId = formatter.formatCellValue(row.getCell(column)).matches(ModelConstants.ALPHA_NUMERIC_REGEX);
//						if (isValidStudentId) {
//							Optional<StudentDetailsInfoEntity> byStudentIdAndActiveFlag = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE);
//							if (byStudentIdAndActiveFlag.isEmpty()) {
//								cellError("Student ID " + studentId + ": Doesn't Exist.", column, rowNumber, studentAccommodation);
//								error = true;
//								errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
//							} else {
								studentAccommodation.setStudentId(formatter.formatCellValue(row.getCell(column)).trim().toUpperCase());
//							}
//						} else {
//							cellError(ExcelConstants.ALPHANUMERIC_ISSUE, column, rowNumber, studentAccommodation);
//							error = true;
//							errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
//						}
					} else {
						cellError("Student ID should not be empty ", column, rowNumber, studentAccommodation);
						error = true;
						errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					}
					column++;

					// Student Name

					if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
						studentAccommodation.setStudentName(CommonService.capitalizeEachWord(formatter.formatCellValue(row.getCell(column)).trim()));
					}
					column++;

					//if faculty
					if(isFacultyLoggedin){
						// appointment from date
						if (row.getCell(column) != null && StringUtils.isNotEmpty(row.getCell(column).toString().trim())) {
							Cell appointmentFromCell = row.getCell(column);
							LocalDate appointmentFrom = null;
							boolean dateFormat = true;
						    try {
						    	appointmentFrom = appointmentFromCell.getLocalDateTimeCellValue().toLocalDate();
						    }  catch (Exception e) {
								cellError(ExcelConstants.ENTER_SPECIFIC_FORMAT, column, rowNumber,
										studentAccommodation);
								error = true;
								errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
						        dateFormat = false;
	                        }
							if (dateFormat && appointmentFrom != null) {
									studentAccommodation.setAppointmentFrom(appointmentFrom);
							}
						}
						column++;

						// appointment to date
						if (row.getCell(column) != null && StringUtils.isNotEmpty(row.getCell(column).toString().trim())) {
							Cell appointmentToDateCell = row.getCell(column);
							LocalDate appointmentToDate = null;
							boolean dateFormat = true;
						    try {
						    	appointmentToDate = appointmentToDateCell.getLocalDateTimeCellValue().toLocalDate();
						    }  catch (Exception e) {
								cellError(ExcelConstants.ENTER_SPECIFIC_FORMAT, column, rowNumber,
										studentAccommodation);
								error = true;
								errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
						        dateFormat = false;
	                        }
							if (dateFormat && appointmentToDate != null) {
									studentAccommodation.setAppointmentTo(appointmentToDate);
							}
						}
						column++;
					}

					// stay from date
					if (row.getCell(column) != null && StringUtils.isNotEmpty(row.getCell(column).toString().trim())) {
						Cell stayDateCell = row.getCell(column);
						LocalDate staydate = null;
						boolean dateFormat = true;
					    try {
					    	staydate = stayDateCell.getLocalDateTimeCellValue().toLocalDate();
					    }  catch (Exception e) {
							cellError(ExcelConstants.ENTER_SPECIFIC_FORMAT, column, rowNumber,
									studentAccommodation);
							error = true;
							errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					        dateFormat = false;
                        }
						if (dateFormat && staydate != null) {
								studentAccommodation.setStayFrom(staydate);
						}
					} else {
						cellError("Stay From  should not be empty", column, rowNumber, studentAccommodation);
						error = true;
						errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					}
					column++;

					// stay to date
					if (row.getCell(column) != null && StringUtils.isNotEmpty(row.getCell(column).toString().trim())) {
						Cell stayToCell = row.getCell(column);
						LocalDate stayTodate = null;
						boolean dateFormat = true;
					    try {
					    	stayTodate = stayToCell.getLocalDateTimeCellValue().toLocalDate();
					    }  catch (Exception e) {
							cellError(ExcelConstants.ENTER_SPECIFIC_FORMAT, column, rowNumber,
									studentAccommodation);
							error = true;
							errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					        dateFormat = false;
                        }
						if (dateFormat && stayTodate != null) {
								studentAccommodation.setStayTo(stayTodate);
						}
					} else {
						cellError("Stay To  should not be empty", column, rowNumber, studentAccommodation);
						error = true;
						errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					}
					column++;

					// Dining
					if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
						String stringCellValue = row.getCell(column).getStringCellValue();
						boolean contains = Arrays.asList(ModelConstants.FLAG_LIST).contains(stringCellValue);
						if (!contains) {
							cellError("Dining should be either 'Y' or 'N'", column, rowNumber, studentAccommodation);
							error = true;
							errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
						} else {
							studentAccommodation.setDining(stringCellValue);
						}
					} else {
						cellError("Dining should not be empty", column, rowNumber, studentAccommodation);
						error = true;
						errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
					}
					column++;
					

					if (!isFacultyLoggedin) {
						// validating authority Name
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							studentAccommodation
									.setValidatingAuthorityName(formatter.formatCellValue(row.getCell(column)).trim());
						}
						column++;

						// validating authority Email
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							studentAccommodation
									.setValidatingAuthorityEmail(formatter.formatCellValue(row.getCell(column)).trim());
						}
						column++;

						// Student designation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							studentAccommodation
									.setStudentDesignation(formatter.formatCellValue(row.getCell(column)).trim());
						}
						column++;

						// RolePlayedDuringStay
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							studentAccommodation
									.setRolePlayedDuringStay(formatter.formatCellValue(row.getCell(column)).trim());
						}
						column++;
					}

                    // Gender
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                    	studentAccommodation.setGender(row.getCell(column).getStringCellValue());
                        boolean containsGender = Arrays.asList(ModelConstants.GENDER_LIST).contains(studentAccommodation.getGender());
                        if (!containsGender) {
                            cellError("Gender should be either 'M' or 'F'", column, rowNumber, studentAccommodation);
                            error = true;
                            errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
                        }
                    } else {
                        cellError("Gender should not be empty", column, rowNumber, studentAccommodation);
                        error = true;
                        errorDetails.append("-").append(studentAccommodation.getExcelErrorMsg()).append("\n");
                    }
                    column++;
                    
					// Add errors for the row to the studentBulkInfoDto if any
					if (error) {
						studentBulkInfoDto.getErrorList().add(ModelConstants.ROW + rowNumber + ": " + errorDetails);
					} else {
						studentBulkAccommodationList.add(studentAccommodation);
					}
					rowNumber++;
				}


			}
		}
		return studentBulkInfoDto;
	}

	private void saveBulkAccommodation(List<StudentBulkAccommodationDto> studentBulkAccommodationList, StudentBulkAccommodationDto studentBulkAccommodationDto,
									   String userName, String userRole, HttpServletRequest request) {
		try {
			if (studentBulkAccommodationDto.getId() == null && studentBulkAccommodationList.isEmpty()){
				return;
			}

			StudentMasterBulkAppointmentEntity entity = new StudentMasterBulkAppointmentEntity();
			if (studentBulkAccommodationDto.getId() != null) {
				Optional<StudentMasterBulkAppointmentEntity> entityOpt = studentMasterBulkAppointmentRepository.findById(studentBulkAccommodationDto.getId());
				entity = entityOpt.orElseGet(StudentMasterBulkAppointmentEntity::new);
			}

			if(studentBulkAccommodationDto.getFile() != null && !studentBulkAccommodationDto.getFile().isEmpty())  {
				String originalFilename = studentBulkAccommodationDto.getFile().getOriginalFilename();

				int lastDotIndex = originalFilename.lastIndexOf(".");
				String fileName = originalFilename.substring(0, lastDotIndex);
				String fileSuffix = originalFilename.substring(lastDotIndex);


				MCrypt mcrypt = new MCrypt();
				String date = GregorianCalendar.getInstance().get(GregorianCalendar.DATE) + "-"
						+ (GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1) + "-"
						+ GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) + " "
						+ GregorianCalendar.getInstance().get(GregorianCalendar.HOUR) + ":"
						+ GregorianCalendar.getInstance().get(GregorianCalendar.MINUTE) + ":"
						+ GregorianCalendar.getInstance().get(GregorianCalendar.SECOND) + "."
						+ GregorianCalendar.getInstance().get(GregorianCalendar.MILLISECOND);

				String uploadFileName = MCrypt
						.bytesToHex(mcrypt.encrypt(ModelConstants.EMPTY_STRING + fileName + ModelConstants.SPACE + date  + fileSuffix));

				entity.setFileName(uploadFileName);
			}


			entity.setEventName(studentBulkAccommodationDto.getEventName());
			entity.setDescription(studentBulkAccommodationDto.getDescription());
			if(studentBulkAccommodationDto.getEventFromDate() != null)
				entity.setFromDate(studentBulkAccommodationDto.getEventFromDate());
			if(studentBulkAccommodationDto.getEventToDate() != null)
				entity.setToDate(studentBulkAccommodationDto.getEventToDate());
			entity.setStudentCount(studentBulkAccommodationList.size());
			if(isFacultyLoggedin) {
				entity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
			} else {
				entity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());

			}

			StudentMasterBulkAppointmentEntity savedMasterEntity = studentMasterBulkAppointmentRepository.save(entity);

			List<StudentBulkAppointmentEntity> studentRequestList = new ArrayList<>();
			List<String> remarkList = new ArrayList<>();
			for(StudentBulkAccommodationDto studentDto: studentBulkAccommodationList) {

				StudentBulkAppointmentEntity studentRequestEntity = new StudentBulkAppointmentEntity();
				if(isFacultyLoggedin) {
					studentRequestEntity = StudentBulkAppointmentEntity.builder().studentMasterBulkAppointment(savedMasterEntity)
							.studentId(studentDto.getStudentId())
							.stayFrom(studentDto.getStayFrom())
							.stayTo(studentDto.getStayTo())
							.dining(studentDto.getDining())
							.appointmentFrom(studentDto.getAppointmentFrom())
							.appointmentTo(studentDto.getAppointmentTo())
							.gender(studentDto.getGender())
							.build();
				} else {
					studentRequestEntity = StudentBulkAppointmentEntity.builder().studentMasterBulkAppointment(savedMasterEntity)
							.studentId(studentDto.getStudentId())
							.stayFrom(studentDto.getStayFrom())
							.stayTo(studentDto.getStayTo())
							.dining(studentDto.getDining())
							.validatingAuthority(studentDto.getValidatingAuthorityName())
							.validatingAuthorityEmail(studentDto.getValidatingAuthorityEmail())
							.studentDesignation(studentDto.getStudentDesignation())
							.rolePlayedDuringStay(studentDto.getRolePlayedDuringStay())
							.gender(studentDto.getGender())
							.build();
				}

				studentRequestList.add(studentRequestEntity);

				boolean isBlackListed = studentBlackListDetailRepository.blacklistExistsByStudentId(studentDto.getStudentId().toUpperCase(), ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE);
				if(isBlackListed){
					remarkList.add("Remarks Exist  For - "+studentDto.getStudentId().toUpperCase());
				}
			}
			if(studentBulkAccommodationDto.getId() != null && CollectionUtils.isNotEmpty(studentRequestList)) {
				//inactivate existing student data and insert new one
				savedMasterEntity.getStudentBulkAppointments().forEach(d->d.setActiveFlag(ModelConstants.STATUS_INACTIVE));

			}
			if(CollectionUtils.isNotEmpty(studentRequestList)) {
				if(CollectionUtils.isNotEmpty(savedMasterEntity.getStudentBulkAppointments())){
					savedMasterEntity.getStudentBulkAppointments().addAll(studentRequestList);
				} else {
					savedMasterEntity.setStudentBulkAppointments(studentRequestList);
				}
			}


			if(isFacultyLoggedin) {
				List<Rows> rows = studentBulkAccommodationDto.getRows();
				List<StudentBulkAppointmentDetailsEntity> detailList = new ArrayList<>();

				if(studentBulkAccommodationDto.getId() != null) {
					List<Long> rowIdList = rows.stream().map(Rows::getId).toList();
					List<StudentBulkAppointmentDetailsEntity> inactiveList = savedMasterEntity.getStudentBulkAppointmentDetails()
							.stream()
							.peek(d -> {
								if (ModelConstants.STATUS_ACTIVE.equals(d.getActiveFlag()) && !rowIdList.contains(d.getId())) {
									d.setActiveFlag(ModelConstants.STATUS_INACTIVE);
								}
							})
							.filter(d-> ModelConstants.STATUS_INACTIVE.equals(d.getActiveFlag()))
							.toList();
					detailList.addAll(inactiveList);
				}

				for (Rows dto: rows) {
					StudentBulkAppointmentDetailsEntity detailEntity = new StudentBulkAppointmentDetailsEntity();

					if(dto.getId() != null) {
						Optional<StudentBulkAppointmentDetailsEntity> entityOptional = studentBulkAppointmentDetailsRepository.findById(dto.getId());
						detailEntity = entityOptional.orElseGet(StudentBulkAppointmentDetailsEntity::new);
					}

					detailEntity.setStudentMasterBulkAppointment(savedMasterEntity);
					detailEntity.setStayFrom(dto.getStayFrom());
					detailEntity.setStayTo(dto.getStayTo());
					detailEntity.setNoOfMaleParticipants(dto.getMaleParticipants());
					detailEntity.setNoOfFemaleParticipants(dto.getFemaleParticipants());
					detailEntity.setDining(dto.getDining());
					detailEntity.setSessionPeriod(dto.getSession());
                    detailEntity.setBreakfastCount(dto.getBreakfastCount());
                    detailEntity.setLunchCount(dto.getLunchCount());
                    detailEntity.setDinnerCount(dto.getDinnerCount());
					detailList.add(detailEntity);
				}
				if(CollectionUtils.isNotEmpty(detailList)) {
					savedMasterEntity.setStudentBulkAppointmentDetails(detailList);
				}
			}
			studentMasterBulkAppointmentRepository.save(savedMasterEntity);

			//send mail
			sendMailOnNewRequest(userRole, userName, entity, request);

		} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


	private void sendMailOnNewRequest(String userRole, String userName, StudentMasterBulkAppointmentEntity entity, HttpServletRequest request) throws Exception {


		Optional<MailTemplateEntity> templateOpt = Optional.empty();
			templateOpt = mailTemplateRepository.findByMailType(MailTemplateEntity.STUDENT_BULK_ACCOM_UPLOADED);

		String mainSubject = "";
		String uploadedBy = "";
		String email = "";
		if (StringUtils.equalsIgnoreCase(userRole,Constants.USER_ROLE_FACULTY)) {
			mainSubject = messageSource.getMessage("message.mail.subject.bulk.accom.request.faculty", null, Locale.getDefault());
			email= simsConfigDataService.getSimConfigValue(SimsConfigDataService.APPROVAL_MAIL_TO);

			SendMessageDto sendMessageDto = new SendMessageDto();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_MON_YEAR_TIME_FORMAT)
					.withZone(ZoneId.systemDefault()) // or specify a zone like ZoneId.of("America/New_York")
					.withLocale(Locale.ENGLISH);
            Optional<StaffDetailsEntity> facultyOpt = staffDetailsRepository.findByFacultyId(entity.getCreatedBy());
			sendMessageDto.setMessage(facultyOpt.map(StaffDetailsEntity::getFirstName).orElse(entity.getCreatedBy()) + " " +
                    facultyOpt.map(StaffDetailsEntity::getLastName).orElse(entity.getCreatedBy()) +" "+
					messageSource.getMessage("message.upload.request.approve.text",null, Locale.getDefault()) + " " +
					formatter.format(entity.getCreatedAt())+ ". "+
					messageSource.getMessage("message.validate.and.approve",null, Locale.getDefault()));
			sendMessageDto.setSubject(mainSubject);
			String recipient = email;
			sendMessageDto.setRecipient(recipient);

			String finalViewUrl = getFinalViewUrl(request, viewAndUpdateStatusStudentBulkUploadUrl, entity.getId().toString());
			try {
				sendEmail(sendMessageDto, entity, finalViewUrl,request, false);
                sendMessageDto.setRecipient(facultyOpt.map(StaffDetailsEntity::getEmailAddress).orElse(null));
				sendEmail(sendMessageDto, entity, finalViewUrl,request, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;

		} else if(StringUtils.equalsIgnoreCase(userRole,Constants.USER_ROLE_DOST_DEAN)){
			mainSubject = messageSource.getMessage("message.mail.subject.bulk.accom.request.dostdean", null, Locale.getDefault());
			uploadedBy = "Dean(Students) ";
			email = simsConfigDataService.getSimConfigValue(SimsConfigDataService.APPROVAL_MAIL_TO);

		} else if(StringUtils.equalsAnyIgnoreCase(userRole,Constants.CCW, Constants.USER_ROLE_DEAN)){
			mainSubject = messageSource.getMessage("message.mail.subject.bulk.accom.request.ccwdean", null, Locale.getDefault());
			uploadedBy = "Chairman Council Of Wardens ";
			email = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FACULTY_APPROVAL_MAIL_TO);

		}


		if (templateOpt.isPresent()) {
			MailTemplateEntity template = templateOpt.get();
			String content = template.getMailTemplate();
			content = content.replaceAll("#%uploaded_by%#", uploadedBy);
			content = content.replaceAll("#%file_name%#", entity.getEventName());
			boolean mailQueueStatus = mailQueueService.saveMailQueue(mainSubject, commonResponseUtil.getMessage("message.mail.greetings.for"), content,
					email, Constants.STUDENT_BULK_UPLOAD, SecurityCtxUtil.userId(), 1, null, null, null,  null);
		}
	}

	private void cellError(String msg, int column, int rowCount, StudentBulkAccommodationDto studentBulkInfoDto) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0) studentBulkInfoDto.setExcelErrorMsg(msg);
		else studentBulkInfoDto.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		System.out.println(studentBulkInfoDto.getExcelErrorMsg());
		studentBulkInfoDto.setError("error");
	}
	public StudentBulkAccommodationDto getStudentBulkAccommodation(String bulkAppointmentId) {
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(Long.valueOf(bulkAppointmentId));
		if(masterEntity.isEmpty()) {
			return null;
		}
		StudentMasterBulkAppointmentEntity entity = masterEntity.get();
		StudentBulkAccommodationDto dto = new StudentBulkAccommodationDto();
		dto.setId(entity.getId());
		dto.setEventName(entity.getEventName());
		dto.setEventFromDate(entity.getFromDate());
		dto.setEventToDate(entity.getToDate());
		dto.setDescription(entity.getDescription());
		dto.setFileName(entity.getFileName());
		dto.setApprovalStatus(entity.getApprovalStatus());
		dto.setApprovalNotes(entity.getApprovalNotes());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setCount(entity.getStudentCount());
        dto.setBreakfastCount(entity.getBreakfastCount());
        dto.setLunchCount(entity.getLunchCount());
        dto.setDinnerCount(entity.getDinnerCount());
		dto.setFileDownloadLink("public" + downloadFileLink+"/"+entity.getId());
		List<StudentBulkAppointmentDetailsEntity> detailsEntities = entity.getStudentBulkAppointmentDetails();
		List<Rows> rows = detailsEntities.stream()
				.filter(d->ModelConstants.STATUS_ACTIVE.equals(d.getActiveFlag()))
				.map(d -> Rows.builder()
                        .id(d.getId())
                        .stayFrom(d.getStayFrom())
                        .stayTo(d.getStayTo())
                        .maleParticipants(d.getNoOfMaleParticipants())
                        .femaleParticipants(d.getNoOfFemaleParticipants())
                        .dining(d.getDining())
                        .session(d.getSessionPeriod())
                        .breakfastCount(d.getBreakfastCount())
                        .lunchCount(d.getLunchCount())
                        .dinnerCount(d.getDinnerCount())
                        .fromDate(d.getStayFrom() != null ? DateUtility.formatSqlDateToStringWithFormat(Date.valueOf(d.getStayFrom())) : "N/A")
                        .toDate(d.getStayTo() != null ? DateUtility.formatSqlDateToStringWithFormat(Date.valueOf(d.getStayTo())) : "N/A")
                        .build()).collect(Collectors.toList());
		dto.setRows(rows);
		return dto;
	}

	public Workbook downloadFile(String bulkAppointmentId) throws Exception {
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(Long.valueOf(bulkAppointmentId));
		if(masterEntity.isEmpty()) {
			return null;
		}
		StudentMasterBulkAppointmentEntity entity = masterEntity.get();
		List<StudentBulkAppointmentEntity> studentBulkAppointments = entity.getStudentBulkAppointments();
		return prepareFile(entity, studentBulkAppointments);
	}

	public Workbook prepareFile(StudentMasterBulkAppointmentEntity entity, List<StudentBulkAppointmentEntity> studentBulkAppointments) throws Exception {

		XSSFWorkbook workbook = null;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = (XSSFWorkbook) downloadStudentBulkRequestUploadTemplate();
			XSSFSheet sheet = workbook.getSheetAt(0);
			// Create styles using ExcelUtility
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);


			// Populate data rows
			int rowcount = 0;
			int sNo = 1;
			if (CollectionUtils.isNotEmpty(studentBulkAppointments)) {
				for (StudentBulkAppointmentEntity bo : studentBulkAppointments) {
					String studentName = bo.getStudentName();
					Optional<StudentDetailsInfoEntity> byStudentIdAndActiveFlag = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(bo.getStudentId(), ModelConstants.STATUS_ACTIVE);
					if(byStudentIdAndActiveFlag.isPresent()){
						studentName = byStudentIdAndActiveFlag.get().getFirstName() + " " + byStudentIdAndActiveFlag.get().getLastName();
					}
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					if(isFacultyLoggedin) {
						excelUtility.createCell(row, 0, bo.getStudentId(), dataStyle);
						excelUtility.createCell(row, 1, studentName, dataStyle);
						excelUtility.createCell(row, 2, bo.getAppointmentFrom().toString(), dataStyle);
						excelUtility.createCell(row, 3, bo.getAppointmentTo().toString(), dataStyle);
						excelUtility.createCell(row, 4, bo.getStayFrom().toString(), dataStyle);
						excelUtility.createCell(row, 5, bo.getStayTo().toString(), dataStyle);
						excelUtility.createCell(row, 6, bo.getDining(), dataStyle);
					} else {
						excelUtility.createCell(row, 0, bo.getStudentId(), dataStyle);
						excelUtility.createCell(row, 1, studentName, dataStyle);
						excelUtility.createCell(row, 2, bo.getStayFrom().toString(), dataStyle);
						excelUtility.createCell(row, 3, bo.getStayTo().toString(), dataStyle);
						excelUtility.createCell(row, 4, bo.getDining(), dataStyle);
						excelUtility.createCell(row, 5, bo.getValidatingAuthority(), dataStyle);
						excelUtility.createCell(row, 6, bo.getValidatingAuthorityEmail(), dataStyle);
						excelUtility.createCell(row, 7, bo.getStudentDesignation(), dataStyle);
						excelUtility.createCell(row, 8, bo.getRolePlayedDuringStay(), dataStyle);
					}
					sNo++;
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating report", exception);
		}
		return workbook;
	}

	public Boolean updateStatus(String bulkAppointmentId, StatusUpdateDto statusUpdateDto, HttpServletRequest request) {
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(Long.valueOf(bulkAppointmentId));
		if(masterEntity.isEmpty()) {
			return null;
		}
		StudentMasterBulkAppointmentEntity entity = masterEntity.get();
		if(StringUtils.equalsAnyIgnoreCase(statusUpdateDto.getApprovalStatus(),WorkflowStatus.APPROVE.getStatus(),WorkflowStatus.REJECT_REVERSAL.getStatus())) {
			entity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());
		} else if(StringUtils.equalsAnyIgnoreCase(statusUpdateDto.getApprovalStatus(),WorkflowStatus.REJECT.getStatus())){
			entity.setApprovalStatus(WorkflowStatus.REJECTED.getStatus());
		} else if(StringUtils.equalsAnyIgnoreCase(statusUpdateDto.getApprovalStatus(),WorkflowStatus.APPROVE_WITH_CONDITION.getStatus())){
			entity.setApprovalStatus(WorkflowStatus.APPROVED_WITH_CONDITION.getStatus());
		}
		entity.setApprovalNotes(statusUpdateDto.getApprovalNotes());
		StudentMasterBulkAppointmentEntity savedEntity = studentMasterBulkAppointmentRepository.save(entity);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_MON_YEAR_TIME_FORMAT)
				.withZone(ZoneId.systemDefault()) // or specify a zone like ZoneId.of("America/New_York")
				.withLocale(Locale.ENGLISH);

		SendMessageDto sendMessageDto = new SendMessageDto();
		sendMessageDto.setMessage(
				messageSource.getMessage("message.upload.request.submitted.update.text", null, Locale.getDefault())
				+formatter.format(entity.getCreatedAt())
				+ " "+messageSource.getMessage("message.upload.request.has.been.text", null, Locale.getDefault()) +" "+
				entity.getApprovalStatus()
				+ " "+messageSource.getMessage("message.upload.request.by.text", null, Locale.getDefault()) +" "+
				Constants.USER_ROLE_CCW_DEAN+" ,"+
				entity.getModifiedBy());

		sendMessageDto.setSubject(messageSource.getMessage("message.mail.subject.bulk.accom.request", null, Locale.getDefault()) + entity.getApprovalStatus());
		Optional<StaffDetailsEntity> facultyOpt = staffDetailsRepository.findByFacultyId(entity.getCreatedBy());
		String recipient = facultyOpt.map(StaffDetailsEntity::getEmailAddress).orElse(null);
		sendMessageDto.setRecipient(recipient);
		String finalViewUrl = getFinalViewUrl(request, viewStudentBulkUploadUrl, entity.getId().toString());
		try {
			sendEmail(sendMessageDto, entity, finalViewUrl,request, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
	}

	public Boolean deleteBulkAccommodation(String bulkAppointmentId) throws RecordNotExistsException {
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(Long.valueOf(bulkAppointmentId));
		if(masterEntity.isEmpty()) {
			return null;
		}
		StudentMasterBulkAppointmentEntity entity = masterEntity.get();
		entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
		studentMasterBulkAppointmentRepository.save(entity);
		return true;
	}

	public Boolean sendMessage(String bulkAppointmentId, SendMessageDto sendMessageDto, HttpServletRequest request){
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(Long.valueOf(bulkAppointmentId));
		if(masterEntity.isEmpty()) {
			return null;
		}
		Optional<StaffDetailsEntity> facultyOpt = staffDetailsRepository.findByFacultyId(masterEntity.get().getCreatedBy());
		String recipient = facultyOpt.map(StaffDetailsEntity::getEmailAddress).orElse(null);
		sendMessageDto.setRecipient(recipient);

		String finalViewUrl = getFinalViewUrl(request, editStudentBulkUploadUrl, masterEntity.get().getId().toString());
		try {
			sendEmail(sendMessageDto, masterEntity.get(), finalViewUrl, request, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
	}


	void sendEmail(SendMessageDto sendMessageDto, StudentMasterBulkAppointmentEntity entity, String finalViewUrl, HttpServletRequest request, boolean initMailToFaculty) throws Exception {
		String content = ModelConstants.EMPTY_STRING;
        String email = sendMessageDto.getRecipient();
        if (initMailToFaculty) {
            Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
                    .findByMailType(MailTemplateEntity.STUDENT_BULK_ACCOMMODATION_FACULTY_MAIL);
            if (templateOpt.isPresent()) {
                MailTemplateEntity template = templateOpt.get();
                content = template.getMailTemplate();
            }
        } else {
            Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
                    .findByMailType(MailTemplateEntity.STUDENT_BULK_UPLOAD_SEND_MESSAGE);
            if (templateOpt.isPresent()) {
                MailTemplateEntity template = templateOpt.get();
                String subject = template.getMailSubject();
                content = template.getMailTemplate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

                content = content.replaceAll("#%message%#", sendMessageDto.getMessage());
                content = content.replaceAll("#%event_name%#", entity.getEventName());
                content = content.replaceAll("#%description%#", entity.getDescription());
                content = content.replaceAll("#%file_name%#", entity.getFileName() != null ? entity.getFileName() : "N/A");
                content = content.replaceAll("#%from_date%#", formatter.format(entity.getFromDate()));
                content = content.replaceAll("#%to_date%#", formatter.format(entity.getToDate()));
                content = content.replaceAll("#%approval_status%#", entity.getApprovalStatus() != null ? entity.getApprovalStatus() : "N/A");
                content = content.replaceAll("#%approval_notes%#", entity.getApprovalNotes() != null ? entity.getApprovalNotes() : "N/A");
                content = content.replaceAll("#%view_string%#", finalViewUrl);
            }
        }
        mailQueueService.saveMailQueue(sendMessageDto.getSubject(), commonResponseUtil.getMessage("message.mail.greetings.for"), content,
                email, Constants.STUDENT_BULK_UPLOAD, SecurityCtxUtil.userId().toUpperCase(), 1,
                null, null, null, null);
    }
	private String getFinalViewUrl(HttpServletRequest request, String controllerAPI, String id) {
//		String serverName = request.getServerName();
//		int serverPort = request.getServerPort();
//		String contextPath = request.getContextPath();
		String baseUrl = Utility.getDomainUrl(request);
		String publicApi = simsConfigDataService.getSimConfigValue(SimsConfigDataService.PUBLIC_API);
		publicApi = StringUtils.isNotEmpty(publicApi) ? publicApi : publicApiUrl;
		return baseUrl + publicApi + controllerAPI  + "/" + id;
	}

	public Boolean updateAndApprove(StudentBulkAccommodationDto studentBulkAccommodationDto, String status, HttpServletRequest request) {
		Optional<StudentMasterBulkAppointmentEntity> masterEntity =
				studentMasterBulkAppointmentRepository.findById(studentBulkAccommodationDto.getId());
		if(masterEntity.isEmpty()) {
			return null;
		}
		StudentMasterBulkAppointmentEntity entity = masterEntity.get();

		if(studentBulkAccommodationDto.getEventFromDate() != null)
		entity.setFromDate(studentBulkAccommodationDto.getEventFromDate());

		if(studentBulkAccommodationDto.getEventToDate() != null)
		entity.setToDate(studentBulkAccommodationDto.getEventToDate());


		if(StringUtils.equalsAnyIgnoreCase(status,WorkflowStatus.APPROVE.getStatus(),WorkflowStatus.REJECT_REVERSAL.getStatus())) {
			entity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());
		} else if(StringUtils.equalsAnyIgnoreCase(status,WorkflowStatus.REJECT.getStatus())){
			entity.setApprovalStatus(WorkflowStatus.REJECTED.getStatus());
		} else if(StringUtils.equalsAnyIgnoreCase(status,WorkflowStatus.APPROVE_WITH_CONDITION.getStatus())){
			entity.setApprovalStatus(WorkflowStatus.APPROVED_WITH_CONDITION.getStatus());
		}
		if(StringUtils.isNotBlank(studentBulkAccommodationDto.getApprovalNotes())) {
			entity.setApprovalNotes(studentBulkAccommodationDto.getApprovalNotes());
		}
		StudentMasterBulkAppointmentEntity savedEntity = studentMasterBulkAppointmentRepository.save(entity);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_MON_YEAR_TIME_FORMAT)
				.withZone(ZoneId.systemDefault()) // or specify a zone like ZoneId.of("America/New_York")
				.withLocale(Locale.ENGLISH);

		SendMessageDto sendMessageDto = new SendMessageDto();
		String userName; try { userName = SecurityCtxUtil.userName(); } catch (Exception e) { userName = RoleEnum.CCW_DEAN.getValue(); }

		sendMessageDto.setMessage(
				messageSource.getMessage("message.upload.request.submitted.update.text", null, Locale.getDefault())
						+formatter.format(entity.getCreatedAt())
						+ " "+messageSource.getMessage("message.upload.request.has.been.text", null, Locale.getDefault()) +" "+
						entity.getApprovalStatus()
						+ " "+messageSource.getMessage("message.upload.request.by.text", null, Locale.getDefault()) +" ,"+
						userName);

		sendMessageDto.setSubject(messageSource.getMessage("message.mail.subject.bulk.accom.request", null, Locale.getDefault()) + entity.getApprovalStatus());

		Optional<StaffDetailsEntity> facultyOpt = staffDetailsRepository.findByFacultyId(entity.getCreatedBy());
		String recipient = facultyOpt.map(StaffDetailsEntity::getEmailAddress).orElse(null);
		sendMessageDto.setRecipient(recipient);
		String finalViewUrl = getFinalViewUrl(request, viewStudentBulkUploadUrl, entity.getId().toString());
		try {
			sendEmail(sendMessageDto, entity, finalViewUrl, request, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}

