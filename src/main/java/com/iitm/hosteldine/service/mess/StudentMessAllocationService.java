package com.iitm.hosteldine.service.mess;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import com.iitm.hosteldine.dto.hostel.StudentsHostelAllotmentDto;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.constant.mess.MessAllotmentConstants;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.util.ExcelUtility;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentMessAllocationService {

	private final ExcelUtility excelUtility;
	private final MessageSource messageSource;
	private final MessMasterService messMasterService;
	private final MessMasterCommonService messMasterCommonService;
	private final MessMasterRepository messMasterRepository;
	private final StudentMessDetailsRepository studentMessDetailsRepository;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final MessAllottedListService messAllottedListService;
	private final InMemoryLogService logService;
	private final BulkAsyncExecutor bulkAsyncExecutor;
	private final CommonResponseUtil commonResponseUtil;

	public Workbook downloadStudentMessAllocationTemplate(String type) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String sheetName = null;
		String[] headerData = null;
		String[] tempHeaderDataWidth = null;

		if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(type)) {
			sheetName = ExcelConstants.MESS_CHANGE;
			headerData = ExcelConstants.MESS_CHANGE_HEADER_DATA;
			tempHeaderDataWidth = ExcelConstants.MESS_ALLOCATION_HEADER_DATA_WIDTH;
		} else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(type)) {
			sheetName = ExcelConstants.MESS_REMOVAL;
			headerData = ExcelConstants.MESS_REMOVE_HEADER_DATA;
			tempHeaderDataWidth = ExcelConstants.MESS_REMOVE_HEADER_DATA_WIDTH;
		} else if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(type)) {
			sheetName = ExcelConstants.MESS_ALLOCATION;
			headerData = ExcelConstants.MESS_ALLOCATION_HEADER_DATA;
			tempHeaderDataWidth = ExcelConstants.MESS_ALLOCATION_HEADER_DATA_WIDTH;
		}

		final String[] headerDataWidth = tempHeaderDataWidth;
		XSSFSheet sheet = workbook.createSheet(sheetName);

		excelUtility.setDataStyle(workbook);
		Row headerRow = sheet.createRow(0);
		excelUtility.createHeader(headerRow, 0, headerData, workbook);

		IntStream.range(0, headerDataWidth.length).forEach(i -> {
			try {
				sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i]));
			} catch (NumberFormatException e) {
				sheet.setColumnWidth(i, 4000);
			}
		});

		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	public MessAllottedListDTO startBulkUpload(MessAllottedListDTO messAllottedListDTO) {
		addLog(messAllottedListDTO.getLogTag(), 0, commonResponseUtil.getMessage("upload.started"));

		bulkAsyncExecutor.execute(messAllottedListDTO.getLogTag(), () -> {
			try {
				saveStudentMessAllocation(messAllottedListDTO);
			} catch (Exception e) {
				addError(messAllottedListDTO.getLogTag(), 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});

		return messAllottedListDTO;
	}

	@Transactional(rollbackFor = Exception.class)
	public MessAllottedListDTO saveStudentMessAllocation(MessAllottedListDTO messAllottedListDTO)
			throws Exception {
		List<MessAllottedListDTO> messAllottedDtoList = new ArrayList<>();
		MessAllottedListDTO messDetailsDto = new MessAllottedListDTO();
		DataFormatter formatter = new DataFormatter();
		messDetailsDto.setErrorList(new ArrayList<>());
		ArrayList<String> prevIdList = new ArrayList<>();
		String tag = messAllottedListDTO.getLogTag();
		addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.start"));

		try (InputStream is = new ByteArrayInputStream(messAllottedListDTO.getFileBytes());
             Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			// Assuming first row is header
			int rowNumber = 0;

			Row firstRow = sheet.getRow(0);
			ArrayList<String> cellValues = new ArrayList<>();
			int headerColumnCount = WorkflowStatus.REMOVE.getStatus()
					.equalsIgnoreCase(messAllottedListDTO.getAllocationType()) ? 3 : 4;
			for (int i = 0; i < headerColumnCount; i++) {
				Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellValues.add(formatter.formatCellValue(cell));
			}
			
			boolean headerLabelCheck = false;

			if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				boolean isStudentId = cellValues.contains(
						MessAllotmentConstants.STUDENT_ID.getString() + ModelConstants.SPACE + ModelConstants.ASTERISK);
				boolean isMessHead = cellValues.contains(
						MessAllotmentConstants.MESS_HEAD.getString() + ModelConstants.SPACE + ModelConstants.ASTERISK);
				boolean isFromDate = cellValues.contains(MessAllotmentConstants.FROM_DATE.getString()
						+ ModelConstants.SPACE + MessAllotmentConstants.EXCEL_DATE_FORMAT.getString() + ModelConstants.SPACE
						+ ModelConstants.ASTERISK);
				boolean isToDate = cellValues.contains(MessAllotmentConstants.TO_DATE.getString()
						+ ModelConstants.SPACE + MessAllotmentConstants.EXCEL_DATE_FORMAT.getString() + ModelConstants.SPACE
						+ ModelConstants.ASTERISK);
				headerLabelCheck = isStudentId && isMessHead && isFromDate && isToDate;
			} else if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				boolean isStudentId = cellValues.contains(
						MessAllotmentConstants.STUDENT_ID.getString() + ModelConstants.SPACE + ModelConstants.ASTERISK);
				boolean isMessHead = cellValues.contains(MessAllotmentConstants.NEW_MESS_HEAD.getString()
						+ ModelConstants.SPACE + ModelConstants.ASTERISK);
				boolean isFromDate = cellValues.contains(MessAllotmentConstants.EFFECTIVE_FROM_DATE.getString()
						+ ModelConstants.SPACE + MessAllotmentConstants.EXCEL_DATE_FORMAT.getString() + ModelConstants.SPACE
						+ ModelConstants.ASTERISK);
				boolean isToDate = cellValues.contains(MessAllotmentConstants.EFFECTIVE_TO_DATE.getString()
						+ ModelConstants.SPACE + MessAllotmentConstants.EXCEL_DATE_FORMAT.getString() + ModelConstants.SPACE
						+ ModelConstants.ASTERISK);
				headerLabelCheck = isStudentId && isMessHead && isFromDate && isToDate;
			} else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				boolean isStudentId = cellValues.contains(
						MessAllotmentConstants.STUDENT_ID.getString() + ModelConstants.SPACE + ModelConstants.ASTERISK);
				boolean isTillDate = cellValues.contains(MessAllotmentConstants.EFFECTIVE_TILL_DATE.getString()
						+ ModelConstants.SPACE + MessAllotmentConstants.EXCEL_DATE_FORMAT.getString() + ModelConstants.SPACE
						+ ModelConstants.ASTERISK);
				boolean isDescription = cellValues.contains(MessAllotmentConstants.DESCRIPTION.getString());
				headerLabelCheck = isStudentId && isTillDate && isDescription;
			}

			if (headerLabelCheck) {
				int rowCount = ExcelUtility.countNonEmptyRows(sheet);
				boolean hasValidData = false;
				for (Row row : sheet) {
					if (rowCount > rowNumber) {
						if (rowNumber == 0) { // Skip header row
							rowNumber++;
							continue;
						}
						addLog(tag, 0, commonResponseUtil.getMessage("upload.validating.row")+" " +(rowNumber + 1));
						Integer column = 0;
						boolean error = false;
						MessAllottedListDTO dto = new MessAllottedListDTO();
						StringBuilder errorDetails = new StringBuilder();
						boolean isStudentValid = true;

						// Student ID Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell studentIdCell = row.getCell(column);
							String studentId = formatter.formatCellValue(studentIdCell).trim();

							if (!studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
								handleCellError(BulkAllotmentStudentsConstants.ALPHANUMERIC_CHARACTERS_LONG.getString(),
										column, rowNumber, dto, errorDetails);
								error = true;
								isStudentValid = false;
							} else {
								String formattedStudentId = studentId.toUpperCase();

								if (prevIdList.contains(formattedStudentId)) {
									handleCellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
											+ ModelConstants.SPACE + formattedStudentId + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_REPEAT_WITHIN_SAME_EXCEL
													.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
									isStudentValid = false;
								} else {
									prevIdList.add(formattedStudentId);
								}

								if (!studentDetailsInfoRepository
										.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE)
										.isEmpty()) {
									handleCellError(
											BulkAllotmentStudentsConstants.STUDENT_ID_CHANGED.getString() + studentId,
											column, rowNumber, dto, errorDetails);
									error = true;
									isStudentValid = false;
								}

								if (studentDetailsInfoRepository
										.checkStudentIdExist(studentId, ModelConstants.STATUS_ACTIVE).isEmpty()) {
									handleCellError(
											BulkAllotmentStudentsConstants.STUDENT_ID.getString() + ModelConstants.SPACE
													+ studentId
													+ BulkAllotmentStudentsConstants.DOES_NOT_EXIT.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
									isStudentValid = false;
								}
								if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
									if (messageSource.getMessage("message.label.current", null, Locale.getDefault())
											.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())
											&& messAllottedListService.checkStudentInCurrentMessPeriod(studentId)) {
										handleCellError(MessAllotmentConstants.STUDENT_ID_EXIST_IN_MESS_PERIOD.getString()
														+ ModelConstants.SPACE + studentId, column, rowNumber, dto, errorDetails);
										error = true;
										isStudentValid = false;
									} else if (messageSource.getMessage("message.label.next", null, Locale.getDefault())
											.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())
											&& messAllottedListService.checkStudentInNextMessPeriod(studentId)) {
										handleCellError(MessAllotmentConstants.STUDENT_ID_EXIST_IN_MESS_PERIOD.getString()
												+ ModelConstants.SPACE + studentId, column, rowNumber, dto, errorDetails);
										error = true;
										isStudentValid = false;
									}
								} else if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())
										|| WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
									if (!messAllottedListService.checkStudentInCurrentMessPeriod(studentId)) {
										handleCellError(MessAllotmentConstants.STUDENT_ID_NOT_EXIST_IN_MESS_PERIOD.getString()
														+ ModelConstants.SPACE + studentId, column, rowNumber, dto, errorDetails);
										error = true;
										isStudentValid = false;
									}
								}
								dto.setStudentId(formattedStudentId);
							}
						} else {
							handleCellError(
									BulkAllotmentStudentsConstants.STUDENT_ID.getString() + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
									column, rowNumber, dto, errorDetails);
							error = true;
							isStudentValid = false;
						}
						column++;

						if (isStudentValid) {
							if (!WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
								// Mess Name Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell messHeadCell = row.getCell(column);
									String cellValue = messHeadCell.getStringCellValue().trim();
	
									Object messListObj = messAllottedListService.fetchMessListByStudentGender(dto.getStudentId());
	
									if (MessAllotmentConstants.NO_MESS_AVAILABLE.getString().equals(messListObj)) {
										handleCellError(MessAllotmentConstants.NO_MESS_AVAILABLE_FOR_STUDENT_ID.getString()
														+ ModelConstants.SPACE + dto.getStudentId(), column, rowNumber, dto, errorDetails);
										error = true;
									} else {
										@SuppressWarnings("unchecked")
										List<MessMasterDto> messList = (List<MessMasterDto>) messListObj;
	
										boolean matchFound = messList.stream()
												.anyMatch(mess -> mess.getMessHead().equalsIgnoreCase(cellValue));
	
										if (!matchFound) {
											handleCellError(MessAllotmentConstants.MESS.getString() + ModelConstants.SPACE
															+ cellValue + ModelConstants.SPACE
															+ MessAllotmentConstants.IS_INVALID_FOR_STUDENT_ID.getString()
															+ ModelConstants.SPACE + dto.getStudentId(),
															column, rowNumber, dto, errorDetails);
											error = true;
										} else {
											Optional<MessMasterEntity> messInfo = messMasterRepository.getMessMasterDetailsByMessHead(
														cellValue, ModelConstants.STATUS_ACTIVE);
											if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())
													&& messageSource.getMessage("message.label.current", null, Locale.getDefault())
															.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())) {
												MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
												Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepository
														.findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(
																dto.getStudentId(), ModelConstants.STATUS_ACTIVE,
																currentMessPeriod.getId());
	
												if (existingEntity.isPresent()) {
													if (messInfo.isPresent()) {
														if (existingEntity.get().getMessMaster().getId() == messInfo.get().getId()) {
															handleCellError(
																	MessAllotmentConstants.CHANGE_MESS_SHOULD_NOT_BE_SAME_AS_CURRENT_MESS.getString()
																	+ ModelConstants.SPACE + cellValue, column, rowNumber, dto, errorDetails);
															error = true;
														} else {
															dto.setChangeMessId(messInfo.get().getId());
															dto.setMessId(messInfo.get().getId());
														}
													} else {
														handleCellError(
																MessAllotmentConstants.MESS_HEAD_NOT_FOUND.getString()
																		+ ModelConstants.SPACE + cellValue,
																column, rowNumber, dto, errorDetails);
														error = true;
													}
												}
											} else if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
												if (messInfo.isPresent()) {
													dto.setMessId(messInfo.get().getId());
												} else {
													handleCellError( MessAllotmentConstants.MESS_HEAD_NOT_FOUND.getString()
															+ ModelConstants.SPACE + cellValue, column, rowNumber, dto, errorDetails);
													error = true;
												}
											}
										}
									}
								} else {
									handleCellError(
											MessAllotmentConstants.MESS_HEAD.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
							}

							if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
								// From Date Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell fromDateCell = row.getCell(column);
									LocalDate fromDate = null;
									boolean dateFormat = true;
								    try {
								        fromDate = fromDateCell.getLocalDateTimeCellValue().toLocalDate();
								    } catch (Exception e) {
								        handleCellError(
								            MessAllotmentConstants.INVALID_DATE_FORMAT.getString(), 
								            column, rowNumber, dto, errorDetails);
								        error = true;
								        dateFormat = false;
								    }
									if (dateFormat) {
										if (messageSource.getMessage("message.label.current", null, Locale.getDefault())
												.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())) {
											MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
											if (currentMessPeriod != null) {
												if (fromDate.isBefore(LocalDate.now())) {
													handleCellError(MessAllotmentConstants.FROM_DATE_SHOULD_BE_FUTURE_DATE.getString()
															+ ModelConstants.SPACE + fromDate, column, rowNumber, dto, errorDetails);
													error = true;
												} else if (fromDate.isBefore(currentMessPeriod.getDiningFromDate())
														|| fromDate.isAfter(currentMessPeriod.getDiningToDate())) {
													handleCellError(
															MessAllotmentConstants.SELECTED_FROM_DATE_OUT_OF_MESS_PERIOD.getString()
															+ ModelConstants.SPACE + fromDate, column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													dto.setFromDate(fromDate);
													dto.setDiningFromDate(currentMessPeriod.getDiningFromDate());
													dto.setDiningToDate(currentMessPeriod.getDiningToDate());
												}
											} else {
												handleCellError(MessAllotmentConstants.CURRENT_MESS_PERIOD_NOT_CONFIGURED.getString(),
														column, rowNumber, dto, errorDetails);
												error = true;
											}
										} else if (messageSource.getMessage("message.label.next", null, Locale.getDefault())
												.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())) {
											MessMasterControllerDto nextMessPeriod = messMasterCommonService
													.getNextMessPeriod();
											if (nextMessPeriod != null) {
												if (fromDate.isBefore(nextMessPeriod.getDiningFromDate())
														|| fromDate.isAfter(nextMessPeriod.getDiningToDate())) {
													handleCellError(MessAllotmentConstants.SELECTED_FROM_DATE_OUT_OF_MESS_PERIOD.getString()
															+ ModelConstants.SPACE + fromDate, column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													dto.setFromDate(fromDate);
													dto.setDiningFromDate(nextMessPeriod.getDiningFromDate());
													dto.setDiningToDate(nextMessPeriod.getDiningToDate());
												}
											} else {
												handleCellError(MessAllotmentConstants.NEXT_MESS_PERIOD_NOT_CONFIGURED.getString(),
														column, rowNumber, dto, errorDetails);
												error = true;
											}
										}
								    }
								} else {
									handleCellError(
											MessAllotmentConstants.FROM_DATE.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
								// To Date Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell toDateCell = row.getCell(column);
									LocalDate toDate = null;
									boolean dateFormat = true;
									try {
										toDate = toDateCell.getLocalDateTimeCellValue().toLocalDate();
									} catch (Exception e) {
										handleCellError(
												MessAllotmentConstants.INVALID_DATE_FORMAT.getString(),
												column, rowNumber, dto, errorDetails);
										error = true;
										dateFormat = false;
									}
									if (dateFormat) {
										if (dto.getFromDate() != null && toDate.isBefore(dto.getFromDate())) {
											handleCellError(MessAllotmentConstants.TO_DATE_SHOULD_NOT_BE_BEFORE_FROM_DATE.getString()
													+ ModelConstants.SPACE + toDate, column, rowNumber, dto, errorDetails);
											error = true;
										} else if (messageSource.getMessage("message.label.current", null, Locale.getDefault())
												.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())) {
											MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
											if (currentMessPeriod != null) {
												if (toDate.isBefore(currentMessPeriod.getDiningFromDate())
														|| toDate.isAfter(currentMessPeriod.getDiningToDate())) {
													handleCellError(MessAllotmentConstants.SELECTED_TO_DATE_OUT_OF_MESS_PERIOD.getString()
															+ ModelConstants.SPACE + toDate, column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													dto.setToDate(toDate);
												}
											} else {
												handleCellError(MessAllotmentConstants.CURRENT_MESS_PERIOD_NOT_CONFIGURED.getString(),
														column, rowNumber, dto, errorDetails);
												error = true;
											}
										} else if (messageSource.getMessage("message.label.next", null, Locale.getDefault())
												.equalsIgnoreCase(messAllottedListDTO.getMessPeriod())) {
											MessMasterControllerDto nextMessPeriod = messMasterCommonService
													.getNextMessPeriod();
											if (nextMessPeriod != null) {
												if (toDate.isBefore(nextMessPeriod.getDiningFromDate())
														|| toDate.isAfter(nextMessPeriod.getDiningToDate())) {
													handleCellError(MessAllotmentConstants.SELECTED_TO_DATE_OUT_OF_MESS_PERIOD.getString()
															+ ModelConstants.SPACE + toDate, column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													dto.setToDate(toDate);
												}
											} else {
												handleCellError(MessAllotmentConstants.NEXT_MESS_PERIOD_NOT_CONFIGURED.getString(),
														column, rowNumber, dto, errorDetails);
												error = true;
											}
										}
									}
								} else {
									handleCellError(
											MessAllotmentConstants.TO_DATE.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
							} else if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
								// Effective From Date Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell fromDateCell = row.getCell(column);
									LocalDate fromDate = null;
									boolean dateFormat = true;
								    try {
								        fromDate = fromDateCell.getLocalDateTimeCellValue().toLocalDate();
								    } catch (Exception e) {
								        handleCellError(
								            MessAllotmentConstants.INVALID_DATE_FORMAT.getString(), 
								            column, rowNumber, dto, errorDetails);
								        error = true;
								        dateFormat = false;
								    }
									if (dateFormat) {
										MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
										if (currentMessPeriod != null) {
											Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepository
													.findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(
													dto.getStudentId(), ModelConstants.STATUS_ACTIVE, currentMessPeriod.getId());
											if(existingEntity.isPresent()) {
												if (!fromDate.isAfter(existingEntity.get().getChangeFromDate())
														|| !fromDate.isAfter(LocalDate.now())
														|| !fromDate.isAfter(currentMessPeriod.getDiningFromDate())
														|| fromDate.isAfter(currentMessPeriod.getDiningToDate())) {
													handleCellError(MessAllotmentConstants.EFFECTIVE_FROM_DATE_SHOULD_BE_BETWEEN.getString()
																	+ ModelConstants.SPACE
																	+ existingEntity.get().getChangeFromDate().plusDays(1)
																	+ ModelConstants.SPACE
																	+ MessAllotmentConstants.TO.getString()
																	+ ModelConstants.SPACE
																	+ currentMessPeriod.getDiningToDate(),
															column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													Long id = existingEntity.get().getId();
													dto.setId(id);
													dto.setEffectiveFromDate(fromDate);
													dto.setEffectiveTill(fromDate.minusDays(1));
													dto.setFromDate(fromDate);
													dto.setDiningFromDate(currentMessPeriod.getDiningFromDate());
													dto.setDiningToDate(currentMessPeriod.getDiningToDate());
												}
											}
										} else {
											handleCellError(MessAllotmentConstants.CURRENT_MESS_PERIOD_NOT_CONFIGURED.getString(),
													column, rowNumber, dto, errorDetails);
											error = true;
										}
									}
								} else {
									handleCellError(
											MessAllotmentConstants.EFFECTIVE_FROM_DATE.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
								// Effective To Date Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell toDateCell = row.getCell(column);
									LocalDate toDate = null;
									boolean dateFormat = true;
									try {
										toDate = toDateCell.getLocalDateTimeCellValue().toLocalDate();
									} catch (Exception e) {
										handleCellError(
												MessAllotmentConstants.INVALID_DATE_FORMAT.getString(),
												column, rowNumber, dto, errorDetails);
										error = true;
										dateFormat = false;
									}
									if (dateFormat) {
										MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
										if (currentMessPeriod != null) {
											Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepository
													.findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(
															dto.getStudentId(), ModelConstants.STATUS_ACTIVE, currentMessPeriod.getId());
											if (existingEntity.isPresent()) {
												LocalDate effectiveFromDate = dto.getEffectiveFromDate();
												if ((effectiveFromDate != null && toDate.isBefore(effectiveFromDate))) {
													handleCellError(MessAllotmentConstants.EFFECTIVE_TO_DATE_SHOULD_BE_AFTER_FROM_DATE.getString(), column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													dto.setToDate(toDate);
												}
											}
										} else {
											handleCellError(MessAllotmentConstants.CURRENT_MESS_PERIOD_NOT_CONFIGURED.getString(),
													column, rowNumber, dto, errorDetails);
											error = true;
										}
									}
								} else {
									handleCellError(
											MessAllotmentConstants.EFFECTIVE_TO_DATE.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
							} else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
								// Effective Till Date Validation
								if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
									Cell tillDateCell = row.getCell(column);
									LocalDate tillDate = null;
									boolean dateFormat = true;
								    try {
								    	tillDate = tillDateCell.getLocalDateTimeCellValue().toLocalDate();
								    } catch (Exception e) {
								        handleCellError(
								            MessAllotmentConstants.INVALID_DATE_FORMAT.getString(), 
								            column, rowNumber, dto, errorDetails);
								        error = true;
								        dateFormat = false;
								    }
									if (dateFormat) {
										MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
										if (currentMessPeriod != null) {
											Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepository
													.findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(
													dto.getStudentId(), ModelConstants.STATUS_ACTIVE, currentMessPeriod.getId());
											if (existingEntity.isPresent()) {
												if (tillDate.isBefore(existingEntity.get().getChangeFromDate())
														|| tillDate.isBefore(LocalDate.now())
														|| !tillDate.isAfter(currentMessPeriod.getDiningFromDate())
														|| tillDate.isAfter(currentMessPeriod.getDiningToDate())) {
													handleCellError(
															MessAllotmentConstants.EFFECTIVE_TILL_DATE_SHOULD_BE_BETWEEN
																	.getString() + ModelConstants.SPACE
																	+ existingEntity.get().getChangeFromDate()
																	+ ModelConstants.SPACE
																	+ MessAllotmentConstants.TO.getString()
																	+ ModelConstants.SPACE
																	+ currentMessPeriod.getDiningToDate(),
															column, rowNumber, dto, errorDetails);
													error = true;
												} else {
													Long id = existingEntity.get().getId();
													dto.setId(id);
													dto.setEffectiveTill(tillDate);
												}
											}
										} else {
											handleCellError(MessAllotmentConstants.CURRENT_MESS_PERIOD_NOT_CONFIGURED.getString(),
													column, rowNumber, dto, errorDetails);
											error = true;
										}
									}
								} else {
									handleCellError(MessAllotmentConstants.EFFECTIVE_TILL_DATE.getString() + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
													column, rowNumber, dto, errorDetails);
									error = true;
								}
								column++;
							}
						}
						
						if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
							// Description
							if (row.getCell(column) != null) {
								Cell descriptionCell = row.getCell(column);
								String cellValue = formatter.formatCellValue(descriptionCell).trim();
								if (!cellValue.isEmpty()) {
									dto.setDescription(cellValue);
								}
							}
							column++;
						}
						
						if (error) {
							messDetailsDto.getErrorList()
									.add(ModelConstants.ROW + ModelConstants.SPACE + (rowNumber + 1)
											+ ModelConstants.COLAN + ModelConstants.SPACE + errorDetails.toString());
							hasValidData = true;
						} else {
							messAllottedDtoList.add(dto);
							hasValidData = true;
						}
						rowNumber++;
					}
				}
				// If no valid data was processed, show an error message
				if (!hasValidData) {
					messDetailsDto.getErrorList()
							.add(BulkAllotmentStudentsConstants.EMPTY_DATA_IN_EXCEL_FILE.getString());
				}
			} else {
				messDetailsDto.getErrorList().add(MessAllotmentConstants.INVALID_EXCEL_TEMPLATE.getString());
			}

			if (!messDetailsDto.getErrorList().isEmpty()) {
				messDetailsDto.setMessPeriod(messAllottedListDTO.getMessPeriod());
				messDetailsDto.setAllocationType(messAllottedListDTO.getAllocationType());
				addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
				messDetailsDto.getErrorList().forEach(err -> addValidation(tag, 0, err));
				return messDetailsDto;
			}

			if (WorkflowStatus.REGULAR.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				messAllottedListService.saveNewMessDetails(messAllottedDtoList,
						messageSource.getMessage("message.label.next", null, Locale.getDefault()).equalsIgnoreCase(
						messAllottedListDTO.getMessPeriod()) ? messAllottedListService.fetchNextMessPeriodId()
								: messAllottedListService.fetchCurrentMessPeriodId(),tag);
			} else if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				messAllottedListService.saveChangeMessDetails(messAllottedDtoList,tag);
			} else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(messAllottedListDTO.getAllocationType())) {
				messAllottedListService.saveRemoveMessDetails(messAllottedDtoList,tag);
			}
			addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
			addLog(tag, 0, commonResponseUtil.getMessage("upload.success"));
		} catch (Exception e) {
			addError(tag, 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return messDetailsDto;
	}

	private void handleCellError(String errorMessage, int column, int rowNumber, MessAllottedListDTO dto,
			StringBuilder errorDetails) {
		cellError(errorMessage, column, rowNumber, dto);
		dto.setError("error");
		errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg()).append(ModelConstants.NEW_LINE);
	}

	private static void cellError(String msg, int column, int rowCount, MessAllottedListDTO object) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0)
			object.setExcelErrorMsg(msg);
		else
			object.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		object.setError("error");
	}

	public boolean checkCurrentMessPeriod() {
		MessMasterControllerDto dto = messMasterCommonService.getCurrentMessPeriod();
		if (dto == null)
			return false;
		LocalDate currentDate = LocalDate.now();
		System.out.println(!dto.getDiningToDate().isEqual(currentDate));
		return !dto.getDiningToDate().isEqual(currentDate);
	}

	public boolean checkNextMessPeriod() {
		MessMasterControllerDto dto = messMasterCommonService.getNextMessPeriod();
		return dto != null;
	}
	private void addLog(String tag, long sessionId, String msg) {
		logService.addLog(tag, " <-" + sessionId + "-> " + msg);
	}

	private void addError(String tag, long sessionId, String msg,Exception e) {
		logService.addError(tag, " <-" + sessionId + "-> " + msg,e);
	}

	private void addValidation(String tag, long sessionId, String msg) {
		logService.addValidation(tag, " <-" + sessionId + "-> " + msg);
	}
}
