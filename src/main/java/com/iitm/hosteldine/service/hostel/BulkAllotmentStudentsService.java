package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.dto.hostel.StudentsHostelAllotmentDto;
import com.iitm.hosteldine.dto.hostel.UploadReceiptsDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.mapper.hostel.HostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.hostel.StudentHostelRoomVacatingRequestViewRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BulkAllotmentStudentsService {

	private final ExcelUtility excelUtility;
	private final HostelMasterService hostelMasterService;
	private final HostelRoomInfoService hostelRoomInfoService;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final HostelMasterRepository hostelMasterRepository;
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
	private final StudentHostelRoomVacatingRequestViewRepository studentHostelRoomVacatingRequestViewRepository;
	private final InMemoryLogService logService;
	private final BulkAsyncExecutor bulkAsyncExecutor;
	private final CommonResponseUtil commonResponseUtil;

	public Workbook downloadBulkStudentsHostelAllotmentTemplate() {
		String sheetName = ExcelConstants.HOSTEL_ROOM_ALLOTMENT_UPLOAD;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount = 0;
		String[] headerData = ExcelConstants.HOSTEL_ROOM_ALLOTMENT_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.HOSTEL_ROOM_ALLOTMENT_HEADER_DATA_WIDTH;

		/**
		 * Data Style
		 */
		excelUtility.setDataStyle(workbook);
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

		// Create a hidden sheet for dropdown data
		XSSFSheet hiddenSheet = workbook.createSheet("DropdownData");
		workbook.setSheetHidden(workbook.getSheetIndex("DropdownData"), true);

		// Dropdown data for the three columns
		List<String> hostelNames = hostelMasterService.getHostelList().stream().map(HostelMasterDto::getHostelName)
				.collect(Collectors.toList());

		// Dropdown data for the fourth columns
//		List<String> roomNumbers = hostelRoomInfoService.getRoomNoList().stream().collect(Collectors.toList());

		// Dropdown data for the fourth columns
//		List<String> seats = hostelRoomAllotmentRepository.findDistinctSeats().stream().collect(Collectors.toList());

		// Add dropdown data to the hidden sheet
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 1, hostelNames);
//		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 2, roomNumbers);
//		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 3, seats);

		// Add dropdowns to the main sheet columns
		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 1, "DropdownData!$B$1:$B$" + hostelNames.size()); // Hostel Name
//		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 2, "DropdownData!$C$1:$C$" + roomNumbers.size()); // Room Number
//		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 3, "DropdownData!$D$1:$D$" + seats.size()); // Seats

		// Auto-size the columns
		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	public StudentsHostelAllotmentDto startBulkUpload(StudentsHostelAllotmentDto studentsHostelAllotmentDto) {
		addLog(studentsHostelAllotmentDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.started"));

			bulkAsyncExecutor.execute(studentsHostelAllotmentDto.getLogTag(), () -> {
                try {
                    saveStudentsHostelAllotmentBulkUpload(studentsHostelAllotmentDto);
                } catch (Exception e) {
					addError(studentsHostelAllotmentDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            });

		return studentsHostelAllotmentDto;
	}

    @Transactional(rollbackFor = Exception.class)
	public StudentsHostelAllotmentDto saveStudentsHostelAllotmentBulkUpload(
			StudentsHostelAllotmentDto studentsHostelAllotmentDto) throws Exception {
		List<HostelRoomAllotmentInfoEntity> hostelRoomAllotmentList = new ArrayList<>();
		List<HostelRoomAllotmentInfoDto> hostelRoomAllotmentDto = new ArrayList<>();
		StudentsHostelAllotmentDto allotmentDto = new StudentsHostelAllotmentDto();
		DataFormatter formatter = new DataFormatter();
		allotmentDto.setErrorList(new ArrayList<>());
		ArrayList<String> prevIdList = new ArrayList<>();
		String tag=studentsHostelAllotmentDto.getLogTag();

		addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.start"));
		try (InputStream is = new ByteArrayInputStream(studentsHostelAllotmentDto.getFileBytes());
             Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			// Assuming first row is header
			int rowNumber = 0;

			Row firstRow = sheet.getRow(0);
			ArrayList<String> cellValues = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellValues.add(formatter.formatCellValue(cell));
			}
			// Checking labels
			boolean isStudentId = cellValues.contains(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
					+ ModelConstants.SPACE + ModelConstants.ASTERISK);
			boolean isHostelName = cellValues.contains(BulkAllotmentStudentsConstants.HOSTEL_NAME.getString()
					+ ModelConstants.SPACE + ModelConstants.ASTERISK);
			boolean isRoomNumber = cellValues.contains(BulkAllotmentStudentsConstants.ROOM_NUMBER.getString()
					+ ModelConstants.SPACE + ModelConstants.ASTERISK);
			boolean isSeat = cellValues.contains(BulkAllotmentStudentsConstants.SEAT.getString());

			if (isStudentId && isHostelName && isRoomNumber && isSeat) {
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
						StudentsHostelAllotmentDto dto = new StudentsHostelAllotmentDto();
						HostelRoomAllotmentInfoDto allotmentInfoDto = new HostelRoomAllotmentInfoDto();
						StringBuilder errorDetails = new StringBuilder();

						// Student ID Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell studentIdCell = row.getCell(column);
							String studentId = formatter.formatCellValue(studentIdCell).trim();

							if (!studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
								cellError(BulkAllotmentStudentsConstants.ALPHANUMERIC_CHARACTERS_LONG.getString(),
										column, rowNumber, dto);
								error = true;
								errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
										.append(ModelConstants.NEW_LINE);
							} else {
								String formattedStudentId = studentId.toUpperCase();

								if (prevIdList.contains(formattedStudentId)) {
									cellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
											+ ModelConstants.SPACE + formattedStudentId + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_REPEAT_WITHIN_SAME_EXCEL.getString(),
											column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								} else {
									prevIdList.add(formattedStudentId);
								}

								if (!studentDetailsInfoRepository
										.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE)
										.isEmpty()) {
									cellError(BulkAllotmentStudentsConstants.STUDENT_ID_CHANGED.getString() + studentId,
											column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								}

								if (studentDetailsInfoRepository
										.checkStudentIdExist(studentId, ModelConstants.STATUS_ACTIVE).isEmpty()) {
									cellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString() + ModelConstants.SPACE
													+ studentId
													+ BulkAllotmentStudentsConstants.DOES_NOT_EXIT.getString(),
													column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								}

								if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(
										ModelConstants.STATUS_ACTIVE, studentId, ModelConstants.YES)) {
									cellError(BulkAllotmentStudentsConstants.SETTLEMENT_COMPLETED_FOR_THE_STUDENT
											.getString() + studentId, column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								}

								if (!studentHostelRoomVacatingRequestViewRepository
										.checkStudentApprovalVacatingForm(studentId, ModelConstants.STATUS_ACTIVE,
												WorkflowStatus.APPROVED.getStatus(),
												BulkAllotmentStudentsConstants.COURSE_COMPLETED.getString())
										.isEmpty()) {
									cellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
											+ ModelConstants.SPACE + studentId
											+ BulkAllotmentStudentsConstants.APPLIED_VACATING_RECEVIED_APPROVAL_STUDENT
													.getString(),
											column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								}
								allotmentInfoDto.setStudentId(formattedStudentId);
								dto.setStudentId(formattedStudentId);
							}
						} else {
							cellError(
									BulkAllotmentStudentsConstants.STUDENT_ID.getString() + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
									column, rowNumber, dto);
							error = true;
							errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
									.append(ModelConstants.NEW_LINE);
						}
						column++;

						// Hostel Name Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell hostelNameCell = row.getCell(column);
							String cellValue = hostelNameCell.getStringCellValue().trim();

							HostelMasterEntity hostelInfo = hostelMasterRepository.checkHostelExist(cellValue,
									ModelConstants.STATUS_ACTIVE);

							if (hostelInfo != null) {
								dto.setHostelName(hostelInfo.getHostelName());
								dto.setHostelId(hostelInfo.getId());

								Optional<StudentDetailsInfoEntity> studentOptional = studentDetailsInfoRepository
										.checkStudentIdExist(dto.getStudentId(), ModelConstants.STATUS_ACTIVE);

								if (hostelInfo.getHostelName() != null && studentOptional.isPresent()) {
									StudentDetailsInfoEntity studentEntity = studentOptional.get();

									if (!hostelInfo.getHostelGenderType().equals(studentEntity.getGender())) {
										String genderFullForm = studentEntity.getGender().equals(Constants.MALE)
												? Constants.MALE_FULL_FORM
												: Constants.FEMALE_FULL_FORM;

										cellError(BulkAllotmentStudentsConstants.IN_THE.getString()
												+ ModelConstants.SPACE + cellValue + ModelConstants.SPACE
												+ BulkAllotmentStudentsConstants.IN_THE.getString()
												+ ModelConstants.COMMA + ModelConstants.SPACE + genderFullForm
												+ ModelConstants.SPACE
												+ BulkAllotmentStudentsConstants.STUDENT_CANNOT_BE_ALLOCATED
														.getString(),
												column, rowNumber, dto);
										error = true;
										errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
												.append(ModelConstants.NEW_LINE);
									}
								}
							} else {
								cellError(BulkAllotmentStudentsConstants.HOSTEL_NOT_FOUND.getString()
										+ ModelConstants.SPACE + cellValue, column, rowNumber, dto);
								error = true;
								errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
										.append(ModelConstants.NEW_LINE);
							}
						} else {
							cellError(
									BulkAllotmentStudentsConstants.HOSTEL_NAME.getString() + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
									column, rowNumber, dto);
							error = true;
							errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
									.append(ModelConstants.NEW_LINE);
						}
						column++;

						// Room Number Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell roomNoCell = row.getCell(column);
							String roomNo = "";
							// Check if the Room Number Exists
							switch (roomNoCell.getCellType()) {
							case STRING:
								roomNo = roomNoCell.getStringCellValue().trim();
								break;
							case NUMERIC:
								roomNo = String.valueOf((int) roomNoCell.getNumericCellValue());
								break;
							default:
								throw new IllegalStateException(
										BulkAllotmentStudentsConstants.UNEXPECTED_CELL_TYPE.getString()
												+ ModelConstants.SPACE + roomNoCell.getCellType());
							}
							Object[] result = null;
							if (dto.getHostelName() != null && !dto.getHostelName().isEmpty()) {
								result = hostelMasterRepository.getHostelAndFloorAndRoomDetails(dto.getHostelName(),
										roomNo, ModelConstants.STATUS_ACTIVE);

								if (result != null && result.length > 0) {
									Object[] obj = (Object[]) result[0];
									HostelFloorMasterEntity floor = (HostelFloorMasterEntity) obj[1];
									HostelRoomInfoEntity room = (HostelRoomInfoEntity) obj[2];

									allotmentInfoDto.setBuildingId(floor.getId());
									allotmentInfoDto.setRoomId(room.getId());
									dto.setCapacity(room.getCapacity());

									// Seat Validation
									int capacity = room.getCapacity();
									int seatColumnIndex = column + 1; // Assuming seat is the next column
									Cell seatCell = row.getCell(seatColumnIndex);
									String seatValue = (seatCell != null) ? seatCell.getStringCellValue().trim() : "";

									if (capacity == 1 && (seatValue == null || seatValue.isEmpty())) {
										// Auto-assign seat as "A" when capacity is 1 and seat is empty
										seatValue = "A";
									} else if (seatValue == null || seatValue.isEmpty() || seatValue.length() > 2) {
										cellError(BulkAllotmentStudentsConstants.SEAT_NOT_EMPTY_MUST_BE_SINGLE_CHARACTER
												.getString(), seatColumnIndex, rowNumber, dto);
										error = true;
										errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
												.append(ModelConstants.NEW_LINE);
									} else {
										char seatChar = seatValue.charAt(0);
										int seatIndex = seatChar - 'A';

										if (seatIndex >= capacity) {
											StringBuilder allowedSeats = new StringBuilder();
											for (int i = 0; i < capacity; i++) {
												allowedSeats.append((char) ('A' + i))
														.append(i < capacity - 1
																? ModelConstants.COMMA + ModelConstants.SPACE
																: ModelConstants.EMPTY_STRING);
											}
											cellError(
													BulkAllotmentStudentsConstants.CAP_HOSTEL.getString()
															+ ModelConstants.COLAN + ModelConstants.SPACE
															+ dto.getHostelName() + ModelConstants.SPACE
															+ ModelConstants.HYPHEN + ModelConstants.SPACE
															+ BulkAllotmentStudentsConstants.ROOM.getString()
															+ ModelConstants.SPACE + roomNo + ModelConstants.SPACE
															+ BulkAllotmentStudentsConstants.A_CAPACTIY_OF.getString()
															+ ModelConstants.SPACE + capacity
															+ BulkAllotmentStudentsConstants.SEAT_SHOULD_BE.getString()
															+ allowedSeats.toString()
															+ BulkAllotmentStudentsConstants.ONLY.getString(),
													seatColumnIndex, rowNumber, dto);
											error = true;
											errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
													.append(ModelConstants.NEW_LINE);
										}
									}

									// Check if Hostel, Room, and Seat are valid
									if (dto.getHostelName() != null && dto.getCapacity() != null && seatValue != null) {
										Set<String> existingCombinations = new HashSet<>();

										// Step 1: Check for Duplicate (Hostel-RoomNo-Seat) Combinations
										if (isDuplicateHostelRoomSeat(dto.getHostelName(), roomNo, seatValue,
												existingCombinations)) {
											cellError(dto.getHostelName() + ModelConstants.HYPHEN + roomNo
													+ ModelConstants.HYPHEN + seatValue + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SHOULD_NOT_REPEAT.getString(),
													column, rowNumber, dto);
											error = true;
											errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
													.append(ModelConstants.NEW_LINE);
										}
										Integer occupancyStatus = hostelRoomAllotmentRepository.getRoomOccupancyStatus(
												DateUtility.getNowDate(), DateUtility.getNextYearDate(),
												dto.getHostelId(), allotmentInfoDto.getRoomId(), seatValue);

										// Default to 0 if null
										int status = (occupancyStatus != null) ? occupancyStatus : 0;
										
										// Step 2: Check if Room is Vacant (Only for 'Check Allotment', Not for 'Force
										// Allotment')
										if (studentsHostelAllotmentDto.getUploadType() != null
												&& !studentsHostelAllotmentDto.getUploadType().isEmpty()
												&& studentsHostelAllotmentDto.getUploadType().equals(
														BulkAllotmentStudentsConstants.WITHCHECKHOSTEL.getString())
												&& status > 0) {
											cellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
													+ ModelConstants.HYPHEN + ModelConstants.SPACE + dto.getStudentId()
													+ BulkAllotmentStudentsConstants.CAPACITY_EXCEEDS_IN_HOSTEL.getString()
													+ dto.getHostelName() + ModelConstants.COMMA + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.ROOM_NUMBER.getString()
													+ ModelConstants.COLAN + ModelConstants.SPACE + roomNo
													+ ModelConstants.COMMA + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.SEAT.getString()
													+ ModelConstants.COLAN + ModelConstants.SPACE + seatValue
													+ ModelConstants.DOT, column, rowNumber, dto);
											error = true;
											errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
													.append(ModelConstants.NEW_LINE);
										}
									}
									allotmentInfoDto.setSubRoomId(seatValue);

								} else {
									cellError(BulkAllotmentStudentsConstants.ROOM_NUMBER.getString()
													+ ModelConstants.SPACE + roomNo + ModelConstants.SPACE
													+ BulkAllotmentStudentsConstants.DOES_NOT_EXIST_IN_HOSTEL.getString()
													+ ModelConstants.SPACE + dto.getHostelName(),
											column, rowNumber, dto);
									error = true;
									errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
											.append(ModelConstants.NEW_LINE);
								}
							}
						} else {
							cellError(BulkAllotmentStudentsConstants.ROOM_NO_SHOULD_NOT_EMPTY.getString(), column,
									rowNumber, dto);
							error = true;
							errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg())
									.append(ModelConstants.NEW_LINE);
						}
						column++;

						if (error) {
							allotmentDto.getErrorList().add(ModelConstants.ROW + ModelConstants.SPACE + (rowNumber + 1)
									+ ModelConstants.COLAN + ModelConstants.SPACE + errorDetails.toString());
							hasValidData = true;
						} else {
							hostelRoomAllotmentDto.add(allotmentInfoDto);
							hasValidData = true;
						}
						rowNumber++;
					}
				}
				// If no valid data was processed, show an error message
				if (!hasValidData) {
					allotmentDto.getErrorList().add(BulkAllotmentStudentsConstants.EMPTY_DATA_IN_EXCEL_FILE.getString());
				}
			} else {
				allotmentDto.getErrorList().add(BulkAllotmentStudentsConstants.INVALID_EXCEL_TEMPLATE.getString());
			}

			if (!allotmentDto.getErrorList().isEmpty()) {
				addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
				allotmentDto.getErrorList().forEach(err -> addValidation(tag, 0, err));
				return allotmentDto;
			}
            for (HostelRoomAllotmentInfoDto hraiDto : hostelRoomAllotmentDto) {
                Object[] checkAlloment = null;
                checkAlloment = hostelRoomAllotmentRepository.checkAllomentInsertOrUpdate(hraiDto.getStudentId(), ModelConstants.STATUS_ACTIVE);
                hraiDto.setStayFromDate(DateUtility.getNowDate());
				addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+hraiDto.getStudentId().toUpperCase());

				if (checkAlloment != null && checkAlloment.length > 0) {
                    Object[] result = (Object[]) checkAlloment[0];
                    String studentId = result[0] != null ? result[0].toString().trim() : "";
                    String insertOrUpdate = result[2] != null ? result[2].toString().trim() : "";
                    if (insertOrUpdate.equals(Constants.UPDATED)) {
                        hostelRoomAllotmentRepository.findById(Long.parseLong(String.valueOf(result[1])))
                                .ifPresent(existingEntity -> {
                                    existingEntity.setBuildingId(hraiDto.getBuildingId());
                                    existingEntity.setRoomId(hraiDto.getRoomId());
                                    existingEntity.setSubRoomId(hraiDto.getSubRoomId());
                                    hostelRoomAllotmentRepository.save(existingEntity);
                                });
                    } else {
                        HostelRoomAllotmentInfoEntity entity = HostelRoomAllotmentInfoMapper.INSTANCE.toHostelRoomAllotmentInfoEntity(hraiDto);
                        entity.onCreate();
                        hostelRoomAllotmentRepository.save(entity);
                        hostelRoomAllotmentRepository.findById(Long.parseLong(String.valueOf(result[1])))
                                .ifPresent(existingEntity -> {
                                    existingEntity.setShiftedDate(DateUtility.getPreviousDate());
                                    existingEntity.setNewRoomAllotmentId(entity.getRoomAllotmentId());
                                    hostelRoomAllotmentRepository.save(existingEntity);
                                });
                    }
                } else {
                    HostelRoomAllotmentInfoEntity entity = HostelRoomAllotmentInfoMapper.INSTANCE.toHostelRoomAllotmentInfoEntity(hraiDto);
                    entity.onCreate();
                    hostelRoomAllotmentRepository.save(entity);
                }
            }
			addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
			addLog(tag, 0, commonResponseUtil.getMessage("upload.success"));
		} catch (Exception e) {
			addError(tag, 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}


		return allotmentDto;
	}

	public static void cellError(String msg, int column, int rowCount, StudentsHostelAllotmentDto object) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0)
			object.setExcelErrorMsg(msg);
		else
			object.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		object.setError("error");
	}

	private boolean isDuplicateHostelRoomSeat(String hostelName, String roomNo, String seat,
			Set<String> existingCombinations) {
		String combination = hostelName + ModelConstants.HYPHEN + roomNo + ModelConstants.HYPHEN + seat;
		if (existingCombinations.contains(combination)) {
			return true;
		}
		existingCombinations.add(combination);
		return false;
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
